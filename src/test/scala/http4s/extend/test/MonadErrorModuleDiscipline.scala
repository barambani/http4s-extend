package http4s.extend.test

import java.util.concurrent.ForkJoinPool

import cats.MonadError
import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.laws.discipline.MonadErrorTests
import cats.tests.CatsSuite
import http4s.extend.ErrorInvariantMap
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.syntax.monadError._
import http4s.extend.test.laws.checks.ErrorInvariantMapLawsChecks
import http4s.extend.test.laws.implicits.{ArbitraryInstances, CogenInstances, EqInstances}

import scala.concurrent.{ExecutionContext, Future}

final class MonadErrorModuleDiscipline extends CatsSuite with EqInstances with CogenInstances with ArbitraryInstances with Fixtures {

  implicit val futureExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(new ForkJoinPool())

  /**
    * MonadError under test
    */
  val testErrorMap: ErrorInvariantMap[Throwable, TestError] =
    new ErrorInvariantMap[Throwable, TestError] {
      def direct: Throwable => TestError =
        th => TestError(th.getMessage)

      def reverse: TestError => Throwable =
        er => new Throwable(er.error)
    }

  val stringAdapt: MonadError[Future, String] =
    MonadError[Future, Throwable].adaptErrorType[String]

  val futureError: MonadError[Future, TestError] =
    MonadError[Future, Throwable].adaptErrorType[TestError](testErrorMap)

  val ioError: MonadError[IO, TestError] =
    MonadError[IO, Throwable].adaptErrorType[TestError](testErrorMap)

  /**
    * Verification
    */
  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, String]",
    ErrorInvariantMapLawsChecks[Throwable, String].errorInvariantMap
  )

  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, TestError]",
    ErrorInvariantMapLawsChecks[Throwable, TestError](testErrorMap).errorInvariantMap
  )

  checkAll(
    "MonadErrorTests[Future, String]",
    MonadErrorTests[Future, String](stringAdapt).monadError[String, Int, Double]
  )

  checkAll(
    "MonadErrorTests[Future, TestError]",
    MonadErrorTests[Future, TestError](futureError).monadError[Double, Int, String]
  )

  checkAll(
    "MonadErrorTests[IO, TestError]",
    MonadErrorTests[IO, TestError](ioError).monadError[Double, Int, String]
  )
}