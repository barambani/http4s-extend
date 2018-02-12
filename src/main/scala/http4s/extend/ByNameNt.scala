package http4s.extend

import cats.Eval
import cats.Eval.always
import cats.arrow.FunctionK
import cats.effect.IO
import cats.syntax.either._
import monix.eval.{Task => MonixTask}
import monix.execution.Scheduler

import scala.concurrent.{ExecutionContext, Future}
import scalaz.concurrent.{Task => ScalazTask}

sealed trait ByNameNt[F[_], G[_]] {

  def apply[A](fa: => F[A]): G[A]

  def functionK: FunctionK[F, G] =
    λ[FunctionK[F, G]](apply(_))
}

sealed trait ByNameNtInstances {

  implicit def futureToIo(implicit ec: ExecutionContext): ByNameNt[Future, IO] =
    new ByNameNt[Future, IO] {
      def apply[A](fa: => Future[A]): IO[A] =
        IO.fromFuture(IO.eval(always(fa)))
    }

  implicit def monixTaskToIo(implicit s: Scheduler): ByNameNt[MonixTask, IO] =
    new ByNameNt[MonixTask, IO] {
      def apply[A](fa: => MonixTask[A]): IO[A] = fa.toIO
    }

  implicit def scalazTaskToIo: ByNameNt[ScalazTask, IO] =
    new ByNameNt[ScalazTask, IO] {
      def apply[A](fa: => ScalazTask[A]): IO[A] =
        Eval.always(
          Either.catchNonFatal(fa.unsafePerformSync).fold(
            e => IO.raiseError[A](e),
            a => IO.pure(a)
          )
        ).value
    }
}

object ByNameNt extends ByNameNtInstances {
  type ~~>[F[_], G[_]] = ByNameNt[F, G]
}