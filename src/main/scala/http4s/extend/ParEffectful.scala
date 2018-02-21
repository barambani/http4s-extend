package http4s.extend

import cats.Semigroup
import cats.effect.{Effect, IO}
import cats.syntax.apply._
import cats.syntax.either._

import scala.concurrent.ExecutionContext

/**
  * parMap2 and parMap3, parTupled2 and parTupled3 describe the parallel execution of F[_]
  * in parallel. The IO instance is implemented in terms of fs2 async.start. This bit will
  * be simplified a lot when an instance for cats Parallel wil be available to the public
  */
trait ParEffectful[F[_]] {

  def parMap2[A, B, R](fa: =>F[A], fb: =>F[B])(f: (A, B) => R): F[R]

  def parMap3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) => R): F[R] =
    parMap2(fa, parMap2(fb, fc)(Tuple2.apply))((a, bc) => f(a, bc._1, bc._2))

  def parTupled2[A, B, R](fa: F[A], fb: F[B]): F[(A, B)] =
    parMap2(fa, fb)(Tuple2.apply)

  def parTupled3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C]): F[(A, B, C)] =
    parMap3(fa, fb, fc)(Tuple3.apply)
}

sealed trait ParEffectful1Instances {

  implicit def ioEffectfulOp(implicit ev2: Semigroup[Throwable], ec: ExecutionContext): ParEffectful[IO] =
    new ParEffectful[IO] {

      val evidence = Effect[IO]

      def parMap2[A, B, R](fa: =>IO[A], fb: =>IO[B])(f: (A, B) => R): IO[R] =
        (fs2.async.start(fa), fs2.async.start(fb)) mapN {
          (ioa, iob) =>
            evidence.rethrow(
              (ioa.attempt, iob.attempt) mapN {
                case (Right(a), Right(b)) => f(a, b).asRight
                case (Left(ea), Left(eb)) => ev2.combine(ea, eb).asLeft
                case (Left(ea), _)        => ea.asLeft
                case (_, Left(eb))        => eb.asLeft
              }
            )
        } flatMap identity
    }
}

object ParEffectful extends ParEffectful1Instances {
  @inline def apply[F[_]](implicit F: ParEffectful[F]): ParEffectful[F] = F
}