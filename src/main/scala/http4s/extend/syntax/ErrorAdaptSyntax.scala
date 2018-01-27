package http4s.extend.syntax

import http4s.extend.ErrorAdapt

trait ErrorAdaptSyntax {
  implicit final class ErrorAdaptSyntaxOps[F[_], A](anFa: => F[A])(implicit F: ErrorAdapt[F]) {
    def attemptMapLeft[E](errM: Throwable => E): F[Either[E, A]] =
      F.attemptMapLeft(anFa)(errM)
  }
}