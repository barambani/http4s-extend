package http4s.extend.syntax

import http4s.extend.ErrorAdapt

trait ErrorAdaptSyntax {
  implicit def errorAdaptSyntax[F[_] : ErrorAdapt, A](anFa: => F[A]): ErrorAdaptOps[F, A] = new ErrorAdaptOps(anFa)
}

final class ErrorAdaptOps[F[_], A](anFa: => F[A])(implicit F: ErrorAdapt[F]) {
  def attemptMapLeft[E](errM: Throwable => E): F[Either[E, A]] =
    F.attemptMapLeft(anFa)(errM)
}