package http4s.extend.util

import cats.MonadError
import scalaz.concurrent.{Task => ScalazTask}

import scala.util.Either

/**
  * Provides an orphan instance for a `MonadError[ScalazTask, Throwable]`. The only alternative
  * I know about to this is to use shims, but for the moment I prefer not to add a full dependency
  * only for this. This might change in the future
  */
private[extend] trait ThrowableInstances {

  implicit val scalazThrowableMonadError: MonadError[ScalazTask, Throwable] =
    new MonadError[ScalazTask, Throwable] {

      def raiseError[A](e: Throwable): ScalazTask[A] =
        ScalazTask.fail(e)

      def handleErrorWith[A](fa: ScalazTask[A])(f: Throwable => ScalazTask[A]): ScalazTask[A] =
        fa handleWith { case th => f(th) }

      def pure[A](x: A): ScalazTask[A] =
        ScalazTask.now(x)

      def flatMap[A, B](fa: ScalazTask[A])(f: A => ScalazTask[B]): ScalazTask[B] =
        fa flatMap f

      def tailRecM[A, B](a: A)(f: A => ScalazTask[Either[A, B]]): ScalazTask[B] = ???
    }
}
