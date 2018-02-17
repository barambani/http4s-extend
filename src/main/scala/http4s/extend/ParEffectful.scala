package http4s.extend

import cats.effect.IO
import cats.{Apply, Semigroupal}

import scala.concurrent.ExecutionContext

/**
  * parMap2 and parMap3, parTupled2 and parTupled3 describe the parallel execution of F[_]
  * in parallel. The IO instance is implemented in terms of fs2 async.start. This bit will
  * be simplified a lot when an instance for cats Parallel wil be available to the public
  */
trait ParEffectful[F[_]] {

  val semigroupalEvidence: Semigroupal[F]

  def parMap2[A, B, R](fa: F[A], fb: F[B])(f: (A, B) => R): F[R]

  def parMap3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) => R): F[R] =
    parMap2(fa, semigroupalEvidence.product(fb, fc))((a, b) => f(a, b._1, b._2))

  def parTupled2[A, B, R](fa: F[A], fb: F[B]): F[(A, B)] =
    parMap2(fa, fb)(Tuple2.apply)

  def parTupled3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C]): F[(A, B, C)] =
    parMap3(fa, fb, fc)(Tuple3.apply)
}

sealed trait ParEffectfulInstances {

  implicit def ioEffectfulOp(implicit ec: ExecutionContext): ParEffectful[IO] =
    new ParEffectful[IO] {

      val semigroupalEvidence = Semigroupal[IO]
      val composedApply = Apply[IO] compose Apply[IO]

      def parMap2[A, B, R](fa: IO[A], fb: IO[B])(f: (A, B) => R): IO[R] =
        composedApply.map2(fs2.async.start(fa), fs2.async.start(fb))(f) flatMap identity
    }
}

object ParEffectful extends ParEffectfulInstances {
  @inline def apply[F[_]](implicit F: ParEffectful[F]): ParEffectful[F] = F
}