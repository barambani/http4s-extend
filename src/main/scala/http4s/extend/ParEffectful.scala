package http4s.extend

import cats.effect.{ContextShift, Effect, IO}
import cats.syntax.apply._
import cats.syntax.either._
import cats.{Applicative, Id, Monoid, Semigroup, Traverse}
import scalaz.concurrent.{Task => ScalazTask}

/**
  * ParEffectful describes the execution of F[_] in parallel.
  * The IO instance is implemented using fiber and waits for
  * both the computations to complete
  */
trait ParEffectful[F[_]] {
  def parMap2[A, B, R](fa: =>F[A], fb: =>F[B])(f: (A, B) => R): F[R]
}

private[extend] sealed trait ParEffectfulInstances {

  implicit def ioEffectfulOp(implicit ev: Semigroup[Throwable], cs: ContextShift[IO]): ParEffectful[IO] =
    new ParEffectful[IO] {

      val evidence = Effect[IO]

      def parMap2[A, B, R](fa: =>IO[A], fb: =>IO[B])(f: (A, B) => R): IO[R] =
        (for {
          fibA <- (IO.shift *> fa).start
          fibB <- (IO.shift *> fb).start
        } yield
          (fibA.join.attempt, fibB.join.attempt) mapN {
            case (Right(a), Right(b)) => f(a, b).asRight
            case (Left(ea), Left(eb)) => ev.combine(ea, eb).asLeft
            case (Left(ea), _)        => ea.asLeft
            case (_, Left(eb))        => eb.asLeft
          }) flatMap evidence.rethrow
    }

  implicit def scalazTaskEffectfulOp(implicit ev: ParEffectful[IO]): ParEffectful[ScalazTask] =
    new ParEffectful[ScalazTask] {

      import http4s.extend.syntax.byNameNt._

      def parMap2[A, B, R](fa: =>ScalazTask[A], fb: =>ScalazTask[B])(f: (A, B) => R): ScalazTask[R] =
        ev.parMap2(fa.transformTo[IO], fb.transformTo[IO])(f).transformTo[ScalazTask]
    }
}

private[extend] sealed trait ParEffectfulFunctions {

  /**
    * Traverse derived from ParEffectful parMap2. If used with IO in `F[_]` position (summoning
    * the ioEffectfulOp) it will wait for all the effectful computations to complete and
    * will aggregate all the eventual errors through the `Semigroup[Throwable]` provided.
    * See here for possible usage
    *
    * https://github.com/barambani/http4s-poc-api/blob/master/src/main/scala/service/ProductRepo.scala#L33
    */
  def parTraverse[A, B, T[_], F[_]](ta: T[A])(f: A => F[B])(
    implicit
      TR: Traverse[T],
      TM: Monoid[T[B]],
      TA: Applicative[T],
      FE: ParEffectful[F],
      FA: Applicative[F]): F[T[B]] =
    TR.foldM[Id, A, F[T[B]]](ta, FA.pure(TM.empty)) {
      (ftb, a) => FE.parMap2(f(a), ftb) {
        (b, tb) => TM.combine(TA.pure(b), tb)
      }
    }
}

object ParEffectful extends ParEffectfulInstances with ParEffectfulFunctions with ParEffectfulArityFunctions {
  @inline def apply[F[_]](implicit F: ParEffectful[F]): ParEffectful[F] = F
}