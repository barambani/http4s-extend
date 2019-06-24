package http4s.extend.test

import cats.Functor
import cats.effect.IO
import cats.instances.double._
import cats.instances.future.catsStdInstancesForFuture
import cats.instances.int._
import cats.instances.string._
import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.test.laws.checks.ByNameNtLawsChecks
import scalaz.concurrent.{Task => ScalazTask}

import scala.concurrent.Future

final class ByNameNtDiscipline extends MinimalSuite {

  checkAll(
    "[Future ~~> IO]",
    ByNameNtLawsChecks[Future, IO].transformation[String, Int]
  )

  {
    implicit val scalazTaskFunctor: Functor[ScalazTask] =
      new Functor[ScalazTask] {
        def map[A, B](fa: ScalazTask[A])(f: A => B): ScalazTask[B] = fa map f
      }

    checkAll(
      "[ScalazTask ~~> IO]",
      ByNameNtLawsChecks[ScalazTask, IO].transformation[Double, String]
    )
  }
}
