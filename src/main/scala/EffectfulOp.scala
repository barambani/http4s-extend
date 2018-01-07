package http4s.extend

import cats.Monad
import cats.effect.{Effect, IO}

import scala.language.higherKinds
import scala.util.Either

trait EffectfulOp[F[_]] {

  val F: Monad[F]

  def suspend[A](t: => F[A]): F[A]
  def runAsync[A](fa: F[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit]

  def delay[A](t: => A): F[A] =
    suspend(F.pure(t))
}

object EffectfulOp {

  def apply[F[_]](implicit F: EffectfulOp[F]): EffectfulOp[F] = F

  implicit val ioEffectfulOp: EffectfulOp[IO] =
    new EffectfulOp[IO] {

      val F: Monad[IO] = Effect[IO]

      def runAsync[A](fa: IO[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit] =
        Effect[IO].runAsync(fa)(cb)

      def suspend[A](t: => IO[A]): IO[A] =
        Effect[IO].suspend(t)
    }
}
