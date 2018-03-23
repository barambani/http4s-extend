package http4s.extend

import cats.MonadError
import cats.effect.{Effect, IO}

import scala.util.Either

/**
  * Separation between the effectful stack and the monad error
  */
trait Effectful[F[_]] {

  val monadError: MonadError[F, Throwable]

  def suspend[A](t: => F[A]): F[A]

  def runAsync[A](fa: F[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit]

  def async[A](k: (Either[Throwable, A] => Unit) => Unit): F[A]

  def delay[A](t: => A): F[A] =
    suspend(monadError.pure(t))
}

sealed trait EffectfulInstances {

  implicit def ioEffectfulOp: Effectful[IO] =
    new Effectful[IO] {

      val monadError = Effect[IO]

      def runAsync[A](fa: IO[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit] =
        monadError.runAsync(fa)(cb)

      def suspend[A](t: => IO[A]): IO[A] =
        monadError.suspend(t)

      def async[A](k: (Either[Throwable, A] => Unit) => Unit): IO[A] =
        monadError.async(k)
    }
}

object Effectful extends EffectfulInstances {
  @inline def apply[F[_]](implicit F: Effectful[F]): Effectful[F] = F
}
