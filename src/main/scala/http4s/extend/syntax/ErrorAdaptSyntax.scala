package http4s.extend.syntax

import http4s.extend.ErrorAdapt

private[syntax] trait ErrorAdaptSyntax {
  implicit def errorAdaptSyntax[F[_], A](anFa: =>F[A]): ErrorAdaptOps[F, A] = new ErrorAdaptOps(anFa)
}

private[syntax] final class ErrorAdaptOps[F[_], A](anFa: =>F[A]) {
  def attemptMapLeft[E](errM: Throwable => E)(implicit F: ErrorAdapt[F]): F[Either[E, A]] =
    F.attemptMapLeft(anFa)(errM)
}