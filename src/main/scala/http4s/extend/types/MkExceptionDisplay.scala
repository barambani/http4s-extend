package http4s.extend.types

import cats.instances.string._
import cats.{Eq, MonadError}
import http4s.extend.{ExceptionDisplay, Iso, NewType}

object MkExceptionDisplay extends NewType with ExceptionDisplayTypeclassInstances {

  def mk(b: String): T = b.asInstanceOf[T]
  def unMk(t: T): String = t.asInstanceOf[String]
  def mkF[F[_]](fs: F[String]): F[T] = fs.asInstanceOf[F[T]]
}

private[types] sealed trait ExceptionDisplayTypeclassInstances {

  implicit val exceptionDisplayEq: Eq[ExceptionDisplay] =
    Eq.by[ExceptionDisplay, String](ExceptionDisplay.unMk)

  implicit val isoThrowable: Iso[Throwable, ExceptionDisplay] =
    new Iso[Throwable, ExceptionDisplay] {

      import http4s.extend.util.ThrowableModule._

      def to: Throwable => ExceptionDisplay = fullDisplay
      def from: ExceptionDisplay => Throwable = throwableOf
    }

  implicit def monadError[F[_], E](implicit M: MonadError[F, E], I: Iso[E, ExceptionDisplay]): MonadError[F, ExceptionDisplay] =
    new MonadError[F, ExceptionDisplay] {

      def raiseError[A](e: ExceptionDisplay): F[A] =
        (M.raiseError[A] _ compose I.from)(e)

      def handleErrorWith[A](fa: F[A])(f: ExceptionDisplay => F[A]): F[A] =
        M.handleErrorWith(fa)(f compose I.to)

      def pure[A](x: A): F[A] = M.pure(x)
      def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = M.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B] = M.tailRecM(a)(f)
    }
}