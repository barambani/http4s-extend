package http4s.extend

import cats.Monad
import cats.effect.{Effect, IO}

import scala.util.Either

trait Effectful[F[_]] {

  val F: Monad[F]

  def suspend[A](t: => F[A]): F[A]
  def runAsync[A](fa: F[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit]

  def delay[A](t: => A): F[A] =
    suspend(F.pure(t))
}

sealed trait EffectfulInstances {

  implicit val ioEffectfulOp: Effectful[IO] =
    new Effectful[IO] {

      val F: Monad[IO] = Effect[IO]

      def runAsync[A](fa: IO[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit] =
        Effect[IO].runAsync(fa)(cb)

      def suspend[A](t: => IO[A]): IO[A] =
        Effect[IO].suspend(t)
    }
}

object Effectful extends EffectfulInstances {
  @inline def apply[F[_]](implicit F: Effectful[F]): Effectful[F] = F
}
