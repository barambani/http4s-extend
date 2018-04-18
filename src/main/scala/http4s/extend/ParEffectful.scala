package http4s.extend

import cats.effect.{Effect, IO}
import cats.syntax.apply._
import cats.syntax.either._
import cats.{Applicative, Id, Monoid, Semigroup, Traverse}
import scalaz.concurrent.{Task => ScalazTask}

import scala.concurrent.ExecutionContext

/**
  * ParEffectful describes the execution of F[_] in parallel.
  * The IO instance is implemented in terms of fs2 async.start
  */
trait ParEffectful[F[_]] {
  def parMap2[A, B, R](fa: =>F[A], fb: =>F[B])(f: (A, B) => R): F[R]
}

private[extend] sealed trait ParEffectfulInstances {

  implicit def ioEffectfulOp(implicit ev: Semigroup[Throwable], ec: ExecutionContext): ParEffectful[IO] =
    new ParEffectful[IO] {

      val evidence = Effect[IO]

      def parMap2[A, B, R](fa: =>IO[A], fb: =>IO[B])(f: (A, B) => R): IO[R] =
        (fs2.async.start(fa), fs2.async.start(fb)) mapN {
          (ioa, iob) =>
            evidence.rethrow(
              (ioa.attempt, iob.attempt) mapN {
                case (Right(a), Right(b)) => f(a, b).asRight
                case (Left(ea), Left(eb)) => ev.combine(ea, eb).asLeft
                case (Left(ea), _)        => ea.asLeft
                case (_, Left(eb))        => eb.asLeft
              }
            )
        } flatMap identity
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
    * Traverse derived from ParEffectful parMap2. If used with IO in F[_] position it will
    * wait for all the effectful computations to complete and will aggregate all the eventual
    * errors in a CompositeException. See here for possible usage
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