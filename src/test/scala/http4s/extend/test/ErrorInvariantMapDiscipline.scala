package http4s.extend.test

import cats.tests.CatsSuite
import http4s.extend.Algebra.ThrowableCompleteMessage
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.instances.invariant._
import http4s.extend.test.Fixtures.TestError
import http4s.extend.test.Fixtures.instances._
import http4s.extend.test.laws.checks.ErrorInvariantMapLawsChecks
import http4s.extend.test.laws.instances.{ArbitraryInstances, EqInstances}

final class ErrorInvariantMapDiscipline extends CatsSuite with EqInstances with ArbitraryInstances with Fixtures {

  /**
    * Verification
    */
  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, ThrowableCompleteMessage]",
    ErrorInvariantMapLawsChecks[Throwable, ThrowableCompleteMessage].errorInvariantMap
  )

  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, TestError]",
    ErrorInvariantMapLawsChecks[Throwable, TestError](testErrorMap).errorInvariantMap
  )
}