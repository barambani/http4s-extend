package http4s.extend.types

import cats.Applicative
import cats.effect.IO
import http4s.extend.{NewTypeF, NonFailingIO, |}

object MkNonFailingIO extends NewTypeF with NonFailingIOInstances {

  def apply[A](io: IO[Throwable | A]): T[A] = io.asInstanceOf[T[A]]
  def fromIo[A](io: IO[A]): T[A] = apply(io.attempt)
  def fromA[A](a: A): T[A] = fromIo(IO.pure(a))

  implicit final class MkVoidSyntax[A](val t: T[A]) extends AnyVal {

    def unMk(): IO[Throwable | A] =
      t.asInstanceOf[IO[Throwable | A]]

    def absolved(): IO[A] =
      unMk() flatMap IO.fromEither
  }
}

private[types] sealed trait NonFailingIOInstances {

  implicit def nonFailingIoApplicative: Applicative[NonFailingIO] =
    new Applicative[NonFailingIO] {
      def pure[A](x: A): NonFailingIO[A] = ???
      def ap[A, B](ff: NonFailingIO[A => B])(fa: NonFailingIO[A]): NonFailingIO[B] = ???
    }
}