import cats.Eq
import cats.tests.CatsSuite
import http4s.extend.instances.errorInvariantMap._
import laws.checks.ErrorInvariantMapLawsChecks
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

final class ErrorInvariantMapDiscipline extends CatsSuite {

  case class TestError(errorCode: Int)

  /**
    * Equality implicits
    */
  implicit val throwableEq: Eq[Throwable] =
    Eq.by[Throwable, String](_.toString)

  implicit val testErrorEq: Eq[TestError] =
    Eq.by[TestError, Int](_.errorCode)

  /**
    * Arbitrary implicits
    */
  implicit val nonFatalArbitrary: Arbitrary[Throwable] =
    Arbitrary(arbitrary[Exception].map(identity))

  implicit def testErrorArb(implicit AI: Arbitrary[Int]): Arbitrary[TestError] =
    Arbitrary { AI.arbitrary map TestError }

  /**
    * Verification
    */
  checkAll("ErrorInvariantMapLawsChecks[Throwable, String]", ErrorInvariantMapLawsChecks[Throwable, String].errorInvariantMap)
//  checkAll("Error invariant map -> String, Int", ErrorInvariantMapLawsChecks[String, Int].errorInvariantMap)
//  checkAll("Error invariant map -> Throwable, Int", ErrorInvariantMapLawsChecks[Throwable, Int].errorInvariantMap)
//  checkAll("Error invariant map -> Throwable, TestError", ErrorInvariantMapLawsChecks[Throwable, TestError].errorInvariantMap)
}
