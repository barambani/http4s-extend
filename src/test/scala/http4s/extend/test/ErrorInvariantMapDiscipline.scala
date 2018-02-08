package http4s.extend.test

import cats.tests.CatsSuite
import http4s.extend.ExceptionDisplayType._
import http4s.extend.instances.eq._
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.instances.invariant._
import http4s.extend.test.Fixtures.TestError
import http4s.extend.test.Fixtures.instances._
import http4s.extend.test.laws.checks.ErrorInvariantMapLawsChecks
import http4s.extend.test.laws.instances.ArbitraryInstances

final class ErrorInvariantMapDiscipline extends CatsSuite with ArbitraryInstances with Fixtures {

  /**
    * Verification
    */
  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, ThrowableCompleteMessage]",
    ErrorInvariantMapLawsChecks[Throwable, ExceptionDisplay].errorInvariantMap
  )

  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, TestError]",
    ErrorInvariantMapLawsChecks[Throwable, TestError](testErrorMap).errorInvariantMap
  )
}