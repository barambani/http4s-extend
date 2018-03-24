package http4s.extend

import cats.{Applicative, Id, Monoid, Semigroup, Traverse}
import cats.effect.{Effect, IO}
import cats.syntax.apply._
import cats.syntax.either._

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
}

private[extend] sealed trait ParEffectfulFunctions {

  def parMap3[F[_] : ParEffectful, A, B, C, R](fa: =>F[A], fb: =>F[B], fc: =>F[C])(f: (A, B, C) => R): F[R] =
    ParEffectful.parMap2(fa, ParEffectful.parTupled2(fb, fc)) { case (a, (b, c)) => f(a, b, c) }

  def parTupled3[F[_] : ParEffectful, A, B, C](fa: =>F[A], fb: =>F[B], fc: =>F[C]): F[(A, B, C)] =
    parMap3(fa, fb, fc)(Tuple3.apply)

  def parMap4[F[_] : ParEffectful, A1, A2, A3, A4, R](fa1: =>F[A1], fa2: =>F[A2], fa3: =>F[A3], fa4: =>F[A4])(f: (A1, A2, A3, A4) => R): F[R] =
    ParEffectful.parMap2(fa1, ParEffectful.parTupled2(fa2, ParEffectful.parTupled2(fa3, fa4))) { case (a1, (a2, (a3, a4))) => f(a1, a2, a3, a4) }

  def parMap41[F[_] : ParEffectful, A, B, C, D, R](fa: =>F[A], fb: =>F[B], fc: =>F[C], fd: =>F[D])(f: (A, B, C, D) => R): F[R] =
    parMap3(fa, fb, ParEffectful.parTupled2(fc, fd)) { case (a, b, (c, d)) => f(a, b, c, d) }

  def parTupled4[F[_] : ParEffectful, A, B, C, D](fa: =>F[A], fb: =>F[B], fc: =>F[C], fd: =>F[D]): F[(A, B, C, D)] =
    parMap4(fa, fb, fc, fd)(Tuple4.apply)

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