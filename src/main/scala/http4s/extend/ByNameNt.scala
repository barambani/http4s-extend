package http4s.extend

import cats.Eval.always
import cats.arrow.FunctionK
import cats.effect.IO
import cats.instances.future.catsStdInstancesForFuture
import cats.{Eval, Functor}
import scalaz.concurrent.{Task => ScalazTask}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Models a natural transformation between the Functors `F[_]` and `G[_]`.
  *
  * It satisfies the naturality condition. See ByNameNtLaws
  */
sealed trait ByNameNt[F[_], G[_]] {

  implicit def evF: Functor[F]
  implicit def evG: Functor[G]

  /**
    * Gives the Natural Transformation from `F` to `G` for all the types `A` where `F` is called by name
    */
  def apply[A]: (=>F[A]) => G[A]

  def functionK: FunctionK[F, G] =
    λ[FunctionK[F, G]](apply(_))
}

private[extend] sealed trait ByNameNtInstances {

  implicit def futureToIo(implicit ec: ExecutionContext): Future ~~> IO =
    new ByNameNt[Future, IO] {
      val evF = Functor[Future]
      val evG = Functor[IO]

      def apply[A]: (=>Future[A]) => IO[A] =
        IO.fromFuture[A] _ compose IO.eval[Future[A]] compose always
    }

  implicit def scalazTaskToIo: ScalazTask ~~> IO =
    new ByNameNt[ScalazTask, IO] {
      val evF = scalazTaskFunctor
      val evG = Functor[IO]

      def apply[A]: (=>ScalazTask[A]) => IO[A] =
        _.unsafePerformSyncAttempt.fold(
          e => IO.raiseError(e),
          a => IO.pure(a)
        )
    }

  implicit def ioToScalazTask: IO ~~> ScalazTask =
    new ByNameNt[IO, ScalazTask] {
      val evF = Functor[IO]
      val evG = scalazTaskFunctor

      def apply[A]: (=>IO[A]) => ScalazTask[A] =
        fa => Eval.always[ScalazTask[A]](
          fa.attempt.unsafeRunSync.fold(ScalazTask.fail, a => ScalazTask.delay(a))
        ).value
    }

  private def scalazTaskFunctor: Functor[ScalazTask] =
    new Functor[ScalazTask] {
      def map[A, B](fa: ScalazTask[A])(f: A => B): ScalazTask[B] = fa map f
    }
}

object ByNameNt extends ByNameNtInstances {
  type ~~>[F[_], G[_]] = ByNameNt[F, G]
}