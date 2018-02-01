package http4s.extend.test

import java.util.concurrent.ForkJoinPool

import cats.MonadError
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

  val testErrorAdapt: MonadError[Future, TestError] =
    MonadError[Future, Throwable].adaptErrorType[TestError](testErrorMap)

  /**
    * Verification
    */
  checkAll("ErrorInvariantMapLawsChecks[Throwable, String]", ErrorInvariantMapLawsChecks[Throwable, String].errorInvariantMap)
  checkAll("ErrorInvariantMapLawsChecks[Throwable, TestError]", ErrorInvariantMapLawsChecks[Throwable, TestError](testErrorMap).errorInvariantMap)

  checkAll("MonadErrorTests[Future, String]", MonadErrorTests[Future, String](stringAdapt).monadError[String, Boolean, Int])
  checkAll("MonadErrorTests[Future, TestError]", MonadErrorTests[Future, TestError](testErrorAdapt).monadError[Boolean, Int, String])
}