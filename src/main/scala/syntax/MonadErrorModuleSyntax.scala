package com.gilt.lib.syntax

import cats.MonadError
import com.gilt.lib.ErrorConversion
import com.gilt.lib.util.MonadErrorModule

import scala.language.higherKinds

object MonadErrorModuleSyntax {

  implicit final class MonadErrorModuleOps[F[_], E1](me: MonadError[F, E1]) {
    def adaptErrorType[E2](implicit EC: ErrorConversion[E1, E2]): MonadError[F, E2] =
      MonadErrorModule.adaptErrorType(me)
  }
}