package com.gilt.lib.syntax

import com.gilt.lib.ByNameNaturalTransformation.~>

import scala.language.higherKinds

object ByNameNaturalTransformationSyntax {

  implicit final class ByNameNaturalTransformationOps[F[_], G[_], A](fa: F[A])(implicit nt: F ~> G) {
    def lift: G[A] = nt(fa)
    def ~>(): G[A] = lift
  }
}