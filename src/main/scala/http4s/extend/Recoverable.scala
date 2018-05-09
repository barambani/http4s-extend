package http4s.extend

import cats.effect.IO
import cats.syntax.either._
import cats.{Applicative, Monad}

import scala.util.Either

trait Recoverable[E, F[_], G[_]] {

  def M: Monad[F]
  def G: Applicative[G]

  def raiseError[A]: E => F[A]
  def toFallible[A]: G[A] => F[A]
  def recoverWith[A]: F[A] => (E => G[A]) => G[A]

  def recover[A]: F[A] => (E => A) => G[A] =
    fa => f => recoverWith(fa)(G.pure[A] _ compose f)

  def attempt[A]: F[A] => G[Either[E, A]] =
    fa => recover(M.map(fa)(_.asRight[E]))(_.asLeft)

  def absolve[A]: G[Either[E, A]] => F[A] =
    ga => M.flatMap(toFallible(ga)) { _.fold(raiseError, M.pure) }
}

private[extend] sealed trait RecoverableInstances {

  def ioRecoverable[E](
    implicit
      iso: Iso[Throwable, E],
      ev1: Monad[IO],
      ev2: Applicative[NonFailingIO]): Recoverable[E, IO, NonFailingIO] =
    new Recoverable[E, IO, NonFailingIO] {

      val M: Monad[IO] = ev1
      val G: Applicative[NonFailingIO] = ev2

      def raiseError[A]: E => IO[A] =
        IO.raiseError _ compose iso.from

      def toFallible[A]: NonFailingIO[A] => IO[A] =
        nf => nf.unMk() flatMap IO.fromEither

      def recoverWith[A]: IO[A] => (E => NonFailingIO[A]) => NonFailingIO[A] =
        io => f => NonFailingIO.fromIo(
          io.attempt flatMap {
            _.fold(f compose iso.to, NonFailingIO.fromA).absolved()
          }
        )
    }
}

object Recoverable extends RecoverableInstances {
  @inline def apply[E, F[_], G[_]](implicit F: Recoverable[E, F, G]): Recoverable[E, F, G] = F
}