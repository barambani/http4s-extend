import cats.Eq
import cats.tests.CatsSuite
import http4s.extend.ErrorInvariantMap
import http4s.extend.instances.errorInvariantMap._
import laws.checks.ErrorInvariantMapLawsChecks
import org.scalacheck.Arbitrary

final class ErrorInvariantMapDiscipline extends CatsSuite {

  case class TestError(error: String)

  /**
    * Equality implicits
    */
  implicit val throwableEq: Eq[Throwable] =
    Eq.by[Throwable, String](_.getMessage)

  implicit val testErrorEq: Eq[TestError] =
    Eq.by[TestError, String](_.error)

  /**
    * Arbitrary implicits
    */
  implicit def testErrorArb(implicit AI: Arbitrary[String]): Arbitrary[TestError] =
    Arbitrary { AI.arbitrary map TestError }

  /**
    * ErrorInvariantMap under test
    */
  implicit def testErrorMap: ErrorInvariantMap[Throwable, TestError] =
    new ErrorInvariantMap[Throwable, TestError] {
      def direct: Throwable => TestError =
        th => TestError(th.getMessage)

      def reverse: TestError => Throwable =
        te => new Throwable(te.error)
    }

  /**
    * Verification
    */
  checkAll("ErrorInvariantMapLawsChecks[Throwable, String]", ErrorInvariantMapLawsChecks[Throwable, String].errorInvariantMap)
  checkAll("ErrorInvariantMapLawsChecks[Throwable, TestError]", ErrorInvariantMapLawsChecks[Throwable, TestError].errorInvariantMap)
}