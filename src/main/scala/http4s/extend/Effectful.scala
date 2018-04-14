package http4s.extend

import cats.effect.IO

import scala.util.Either
import cats.syntax.either._

/**
  * Separation between the effectful stack and the monad error
  */
trait Effectful[E, F[_]] {

  def unit: F[Unit]

  def point[A]: A => F[A]

  def delay[A]: (=>A) => F[A]

  def fail[A]: E => F[A]

  def suspend[A]: (=>F[A]) => F[A]

  def attempt[A]: F[A] => F[Either[E, A]]

  def absolve[A]: F[Either[E, A]] => F[A]

  def async[A]: ((Either[E, A] => Unit) => Unit) => F[A]

  def runAsync[A]: F[A] => (Either[E, A] => F[Unit]) => F[Unit]
}

private[extend] sealed trait EffectfulInstances {

  implicit val ioEffectful: Effectful[Throwable, IO] =
    new Effectful[Throwable, IO] {

      def unit: IO[Unit] = IO.unit

      def point[A]: A => IO[A] = IO.pure

      def delay[A]: (=>A) => IO[A] = IO.apply

      def fail[A]: Throwable => IO[A] = IO.raiseError

      def suspend[A]: (=>IO[A]) => IO[A] = IO.suspend

      def attempt[A]: IO[A] => IO[Either[Throwable, A]] = _.attempt

      def absolve[A]: IO[Either[Throwable, A]] => IO[A] =
        _ flatMap { _.fold(IO.raiseError, IO.pure) }

      def async[A]: ((Either[Throwable, A] => Unit) => Unit) => IO[A] = IO.async

      def runAsync[A]: IO[A] => (Either[Throwable, A] => IO[Unit]) => IO[Unit] =
        fa => cb => fa runAsync cb
    }

  implicit def stringEffectful(implicit I: Iso[Throwable, ExceptionDisplay]): Effectful[ExceptionDisplay, IO] =
    new Effectful[ExceptionDisplay, IO] {

      def unit: IO[Unit] = IO.unit

      def point[A]: A => IO[A] = IO.pure

      def delay[A]: (=>A) => IO[A] = IO.apply

      def fail[A]: ExceptionDisplay => IO[A] =
        IO.raiseError _ compose I.from

      def suspend[A]: (=>IO[A]) => IO[A] = IO.suspend

      def attempt[A]: IO[A] => IO[Either[ExceptionDisplay, A]] =
        _.attempt map (_ leftMap I.to)

      def absolve[A]: IO[Either[ExceptionDisplay, A]] => IO[A] =
        _ flatMap { _.fold(IO.raiseError _ compose I.from, IO.pure) }

      def async[A]: ((Either[ExceptionDisplay, A] => Unit) => Unit) => IO[A] =
        action => IO.async {
          thrAction => action(
            thrAction compose { (eitherDisp: Either[ExceptionDisplay, A]) => eitherDisp leftMap I.from }
          )
        }

      def runAsync[A]: IO[A] => (Either[ExceptionDisplay, A] => IO[Unit]) => IO[Unit] = ???
    }

  implicit val voidEffectful: Effectful[Void, IO] =
    new Effectful[Void, IO] {

      def unit: IO[Unit] = IO.unit

      def point[A]: A => IO[A] = IO.pure

      def delay[A]: (=>A) => IO[A] = IO.apply

      def fail[A]: Void => IO[A] = ???

      def suspend[A]: (=>IO[A]) => IO[A] = ???

      def attempt[A]: IO[A] => IO[Either[Void, A]] = ???

      def absolve[A]: IO[Either[Void, A]] => IO[A] = ???

      def async[A]: ((Either[Void, A] => Unit) => Unit) => IO[A] = ???

      def runAsync[A]: IO[A] => (Either[Void, A] => IO[Unit]) => IO[Unit] = ???
    }
}

object Effectful extends EffectfulInstances {
  @inline def apply[E, F[_]](implicit F: Effectful[E, F]): Effectful[E, F] = F
}
