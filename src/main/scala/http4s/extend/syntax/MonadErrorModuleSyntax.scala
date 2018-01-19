package http4s.extend.syntax

import cats.MonadError
import http4s.extend.ErrorInvariantMap
import http4s.extend.util.MonadErrorModule

import scala.language.higherKinds

trait MonadErrorModuleSyntax {

  implicit final class MonadErrorModuleOps[F[_], E1](me: MonadError[F, E1]) {
    def adaptErrorType[E2](implicit EC: ErrorInvariantMap[E1, E2]): MonadError[F, E2] =
      MonadErrorModule.adaptErrorType(me)
  }
}