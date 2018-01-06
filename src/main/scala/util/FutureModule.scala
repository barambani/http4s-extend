package com.gilt.lib.util

import cats.syntax.either._
import com.gilt.lib.ByNameNaturalTransformation._
import com.gilt.lib.EffectfulOp
import com.gilt.lib.syntax.ByNameNaturalTransformationSyntax._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait FutureModule {

  def liftTo[F[_] : EffectfulOp : ~>[Future, ?[_]], A](aFuture: Future[A]): F[A] =
    aFuture.lift

  def adaptError[E, A](aFuture: Future[A])(errM: Throwable => E)(implicit ec: ExecutionContext): Future[E Either A] =
    aFuture map (_.asRight[E]) recover { case e: Throwable => errM(e).asLeft }
}

object FutureModule extends FutureModule