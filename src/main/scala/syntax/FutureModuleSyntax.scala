package http4s.extend.syntax

import http4s.extend.ByNameNaturalTransformation.~~>
import http4s.extend.Effectful
import http4s.extend.util.FutureModule

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait FutureModuleSyntax {

  implicit final class FutureModuleOps[A](aFuture: => Future[A]) {

    def liftTo[F[_] : Effectful : ~~>[Future, ?[_]]]: F[A] =
      FutureModule.liftTo(aFuture)

    def adaptError[E](errM: Throwable => E)(implicit ec: ExecutionContext): Future[Either[E, A]] =
      FutureModule.adaptError(aFuture)(errM)
  }
}

object FutureModuleSyntax extends FutureModuleSyntax