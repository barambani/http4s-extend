package http4s.extend.syntax

import cats.MonadError
import http4s.extend.ErrorInvariantMap
import http4s.extend.util.MonadErrorModule

trait MonadErrorModuleSyntax {
  implicit def monadErrorSyntax[F[_], E1](me: MonadError[F, E1]): MonadErrorOps[F, E1] = new MonadErrorOps(me)
}

final class MonadErrorOps[F[_], E1](me: MonadError[F, E1]) {
  def adaptErrorType[E2](implicit EC: ErrorInvariantMap[E1, E2]): MonadError[F, E2] =
    MonadErrorModule.adaptErrorType(me)
}