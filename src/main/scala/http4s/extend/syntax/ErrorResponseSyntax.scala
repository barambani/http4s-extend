package http4s.extend.syntax

import http4s.extend.ErrorResponse
import org.http4s.Response

private[syntax] trait ErrorResponseSyntax {
  implicit def errorResponseSyntax[E](e: E): ErrorResponseOps[E] = new ErrorResponseOps(e)
}

private[syntax] class ErrorResponseOps[E](val e: E) extends AnyVal {
  def responseFor[F[_]](implicit ev: ErrorResponse[F, E]): F[Response[F]] =
    ev.responseFor(e)
}