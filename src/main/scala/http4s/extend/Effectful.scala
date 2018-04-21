package http4s.extend

import cats.effect.IO
import cats.syntax.either._

import scala.util.Either

/**
  * Describes the capability of producing effects. The error is explicit and can be any.
  * An instance for cats.effect.IO and Throwable is provided. It's possible to obtain an instance
  * for any other error `E` providing a valid instance of `Iso[Throwable, E]`.
  *
  * Notice that there's no need to provide an `Iso[Throwable, Throwable]` ad it's automatically
  * put in scope by `Iso`, so an instance of `Effectful[Throwable, IO]` will be always available.
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

  implicit def ioEffectful[E](implicit iso: Iso[Throwable, E]): Effectful[E, IO] =
    new Effectful[E, IO] {

      def unit: IO[Unit] = IO.unit

      def point[A]: A => IO[A] = IO.pure

      def delay[A]: (=>A) => IO[A] = IO.apply

      def fail[A]: E => IO[A] =
        IO.raiseError _ compose iso.from

      def suspend[A]: (=>IO[A]) => IO[A] =
        IO.suspend

      def attempt[A]: IO[A] => IO[Either[E, A]] =
        _.attempt map (_ leftMap iso.to)

      def absolve[A]: IO[Either[E, A]] => IO[A] =
        _ flatMap { _.fold(IO.raiseError _ compose iso.from, IO.pure) }

      def async[A]: ((Either[E, A] => Unit) => Unit) => IO[A] =
        asyncAction => IO.async {
          failingWithThrowable => asyncAction {
            failingWithThrowable compose { (errorOrA: Either[E, A]) => errorOrA leftMap iso.from }
          }
        }

      def runAsync[A]: IO[A] => (Either[E, A] => IO[Unit]) => IO[Unit] =
        io => action => io.runAsync {
          failingWithThrowable => action {
            failingWithThrowable leftMap iso.to
          }
        }
    }
}

object Effectful extends EffectfulInstances {
  @inline def apply[E, F[_]](implicit F: Effectful[E, F]): Effectful[E, F] = F
}
