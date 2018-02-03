package http4s.extend.test

import cats.Eq
import cats.tests.CatsSuite
import http4s.extend.Model.ThrowableCompleteMessage
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.test.Fixtures.TestError
import http4s.extend.test.laws.checks.ErrorInvariantMapLawsChecks
import http4s.extend.test.laws.instances.{ArbitraryInstances, EqInstances}
import org.scalacheck.Arbitrary

final class ErrorInvariantMapDiscipline extends CatsSuite with EqInstances with ArbitraryInstances with Fixtures {

  implicit def testErrorArb(implicit A: Arbitrary[ThrowableCompleteMessage]): Arbitrary[TestError] =
    Arbitrary { A.arbitrary map TestError }

  implicit def testErrorEq: Eq[TestError] =
    Eq.by[TestError, ThrowableCompleteMessage](_.error)

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