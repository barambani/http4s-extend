package http4s.extend

import cats.Eval.always
import cats.arrow.FunctionK
import cats.effect.IO
import cats.instances.future.catsStdInstancesForFuture
import cats.{Eval, Functor}
import monix.eval.{Task => MonixTask}
import monix.execution.Scheduler
import scalaz.concurrent.{Task => ScalazTask}

import scala.concurrent.{ExecutionContext, Future}

sealed trait ByNameNt[F[_], G[_]] {

  implicit def evF: Functor[F]
  implicit def evG: Functor[G]

  def apply[A]: (=>F[A]) => G[A]

  def functionK: FunctionK[F, G] =
    λ[FunctionK[F, G]](apply(_))
}

private[extend] sealed trait ByNameNtInstances {

  implicit def futureToIo(implicit ec: ExecutionContext): ByNameNt[Future, IO] =
    new ByNameNt[Future, IO] {
      val evF = Functor[Future]
      val evG = Functor[IO]

      def apply[A]: (=>Future[A]) => IO[A] =
        IO.fromFuture[A] _ compose IO.eval[Future[A]] compose always
    }

  implicit def monixTaskToIo(implicit s: Scheduler): ByNameNt[MonixTask, IO] =
    new ByNameNt[MonixTask, IO] {
      val evF = Functor[MonixTask]
      val evG = Functor[IO]

      def apply[A]: (=>MonixTask[A]) => IO[A] = _.toIO
    }

  implicit val scalazTaskToIo: ByNameNt[ScalazTask, IO] =
    new ByNameNt[ScalazTask, IO] {
      val evF = new Functor[ScalazTask] {
        def map[A, B](fa: ScalazTask[A])(f: A => B): ScalazTask[B] = fa map f
      }
      val evG = Functor[IO]

      def apply[A]: (=>ScalazTask[A]) => IO[A] =
        fa => Eval.always(
          fa.unsafePerformSyncAttempt.fold(
            e => IO.raiseError(e),
            a => IO.pure(a)
          )
        ).value
    }
}

object ByNameNt extends ByNameNtInstances {
  type ~~>[F[_], G[_]] = ByNameNt[F, G]
}