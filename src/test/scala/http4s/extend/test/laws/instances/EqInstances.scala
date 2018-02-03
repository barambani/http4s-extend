package http4s.extend.test.laws.instances

import cats.Eq
import cats.effect.IO
import cats.instances.string._
import cats.syntax.either._
import http4s.extend.ErrorAdapt
import http4s.extend.Model.ThrowableCompleteMessage
import http4s.extend.syntax.eq._
import http4s.extend.util.ThrowableModule._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait EqInstances {

  implicit def throwableCompleteMessageEq: Eq[ThrowableCompleteMessage] =
    Eq.by[ThrowableCompleteMessage, String](_.message)

  implicit def throwableEq(implicit ev: Eq[ThrowableCompleteMessage]): Eq[Throwable] =
    new Eq[Throwable]{
      def eqv(x: Throwable, y: Throwable): Boolean =
        ev.eqv(completeMessage(x), completeMessage(y))
    }

  implicit def futureEqual[A: Eq](implicit ec: ExecutionContext): Eq[Future[A]] = {

    def futureEither(fa: Future[A]): Future[Either[Throwable, A]] =
      ErrorAdapt[Future].attemptMapLeft(fa)(identity[Throwable])

    new Eq[Future[A]] {
      def eqv(x: Future[A], y: Future[A]): Boolean =
        Await.result(
          futureEither(x) zip futureEither(y) map { case (tx, ty) => tx === ty },
          1.second
        )
    }
  }

  implicit def ioEqual[A: Eq]: Eq[IO[A]] =
    new Eq[IO[A]] {
      def eqv(x: IO[A], y: IO[A]): Boolean = {

        val xv = Either.catchNonFatal(x.attempt.unsafeRunSync())
        val yv = Either.catchNonFatal(y.attempt.unsafeRunSync())

        (xv, yv) match {
          case (Right(vl) , Right(vr))  => vl === vr
          case (Left(el)  , Left(er))   => el === er
          case _                        => false
        }
      }
    }
}
