package http4s.extend

import cats.effect.{Effect, IO}
import cats.{Apply, MonadError, Semigroupal}

import scala.concurrent.ExecutionContext
import scala.util.Either

/**
  * suspend, runAsync, and async wrap the equivalent functions in cats.effect.Effect[F].
  * parMap2 and parMap3, parTupled2 and parTupled3 describe the parallel execution of F[_]
  * in parallel. The IO instance is implemented in terms of fs2 async.start. This bit will
  * be simplified a lot when an instance for cats Parallel wil be available to the public.
  */
trait Effectful[F[_]] {

  val monadError: MonadError[F, Throwable]
  val semigroupEvidence: Semigroupal[F]

  def suspend[A](t: => F[A]): F[A]

  def runAsync[A](fa: F[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit]

  def async[A](k: (Either[Throwable, A] => Unit) => Unit): F[A]

  def parMap2[A, B, R](fa: F[A], fb: F[B])(f: (A, B) => R): F[R]

  def delay[A](t: => A): F[A] =
    suspend(monadError.pure(t))

  def parMap3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) => R): F[R] =
    parMap2(fa, semigroupEvidence.product(fb, fc))((a, b) => f(a, b._1, b._2))

  def parTupled2[A, B, R](fa: F[A], fb: F[B]): F[(A, B)] =
    parMap2(fa, fb)(Tuple2.apply)

  def parTupled3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C]): F[(A, B, C)] =
    parMap3(fa, fb, fc)(Tuple3.apply)
}

sealed trait EffectfulInstances {

  implicit def ioEffectfulOp(implicit ec: ExecutionContext): Effectful[IO] =
    new Effectful[IO] {

      val monadError = Effect[IO]
      val semigroupEvidence = Semigroupal[IO]

      val composedApply = Apply[IO] compose Apply[IO]

      def runAsync[A](fa: IO[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit] =
        monadError.runAsync(fa)(cb)

      def suspend[A](t: => IO[A]): IO[A] =
        monadError.suspend(t)

      def async[A](k: (Either[Throwable, A] => Unit) => Unit): IO[A] =
        monadError.async(k)

      def parMap2[A, B, R](fa: IO[A], fb: IO[B])(f: (A, B) => R): IO[R] =
        composedApply.map2(fs2.async.start(fa), fs2.async.start(fb))(f) flatMap identity
    }
}

object Effectful extends EffectfulInstances {
  @inline def apply[F[_]](implicit F: Effectful[F]): Effectful[F] = F
}
