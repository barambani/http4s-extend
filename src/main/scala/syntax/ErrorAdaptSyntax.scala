package http4s.extend.syntax

import http4s.extend.util.ErrorAdapt

trait ErrorAdaptSyntax {
  implicit final class ErrorAdaptSyntaxOps[F[_], A](anFa: => F[A])(implicit F: ErrorAdapt[F]) {
    def adapt[E](errM: Throwable => E): F[Either[E, A]] =
      F.adapt(anFa)(errM)
  }
}

object ErrorAdaptSyntax extends ErrorAdaptSyntax