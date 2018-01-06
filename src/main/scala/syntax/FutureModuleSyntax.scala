package com.gilt.lib.syntax

import com.gilt.lib.ByNameNaturalTransformation.~>
import com.gilt.lib.EffectfulOp
import com.gilt.lib.util.FutureModule

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait FutureModuleSyntax {

  implicit final class FutureModuleOps[A](aFuture: => Future[A]) {

    def liftTo[F[_] : EffectfulOp : ~>[Future, ?[_]]]: F[A] =
      FutureModule.liftTo(aFuture)

    def adaptError[E](errM: Throwable => E)(implicit ec: ExecutionContext): Future[Either[E, A]] =
      FutureModule.adaptError(aFuture)(errM)
  }
}

object FutureModuleSyntax extends FutureModuleSyntax