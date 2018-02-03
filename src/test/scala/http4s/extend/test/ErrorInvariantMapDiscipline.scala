package http4s.extend.test

import cats.tests.CatsSuite
import http4s.extend.ErrorInvariantMap
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.test.laws.checks.ErrorInvariantMapLawsChecks
import http4s.extend.test.laws.implicits.{ArbitraryInstances, EqInstances}

final class ErrorInvariantMapDiscipline extends CatsSuite with EqInstances with ArbitraryInstances {

  /**
    * ErrorInvariantMap under test
    */
  val testErrorMap: ErrorInvariantMap[Throwable, TestError] =
    new ErrorInvariantMap[Throwable, TestError] {
      def direct: Throwable => TestError =
        th => TestError(th.getMessage)

      def reverse: TestError => Throwable =
        te => new Throwable(te.error)
    }

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
}