package http4s.extend.syntax

import cats.data.{Kleisli, OptionT}
import cats.syntax.flatMap._
import cats.{FlatMap, Functor}
import org.http4s.{HttpService, Request, Response}

private[syntax] trait Http4sServiceSyntax {
  implicit def httpServiceSyntax[F[_]](s: HttpService[F]) = new HttpServiceOps(s)
}

/**
  * Here the parameter's type needs to be esplicitely de-aliased to
  * Kleisli[OptionT[F, ?], Request[F], Response[F]] otherwise the compilation
  * will fail when the parameter is made non private
  */
private[syntax] final class HttpServiceOps[F[_]](val service: Kleisli[OptionT[F, ?], Request[F], Response[F]]) extends AnyVal {

  def runFor(req: Request[F])(implicit F: Functor[F]): F[Response[F]] =
    service.run(req).getOrElse(Response.notFound)

  def runForF(req: F[Request[F]])(implicit F: FlatMap[F]): F[Response[F]] =
    req flatMap (service.run(_).getOrElse(Response.notFound))
}