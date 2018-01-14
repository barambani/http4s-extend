package http4s.extend.util

import cats.syntax.either._
import http4s.extend.ByNameNaturalTransformation._
import http4s.extend.Effectful
import http4s.extend.syntax.ByNameNaturalTransformationSyntax._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait FutureModule {

  def liftTo[F[_] : Effectful : ~~>[Future, ?[_]], A](aFuture: Future[A]): F[A] =
    aFuture.lift

  def adaptError[E, A](aFuture: Future[A])(errM: Throwable => E)(implicit ec: ExecutionContext): Future[E Either A] =
    aFuture map (_.asRight[E]) recover { case e: Throwable => errM(e).asLeft }
}

object FutureModule extends FutureModule