package http4s.extend.test.laws.implicits

import http4s.extend.test.Fixtures
import org.scalacheck.Arbitrary

trait ArbitraryInstances extends Fixtures {

  implicit def testErrorArb(implicit AI: Arbitrary[String]): Arbitrary[TestError] =
    Arbitrary { AI.arbitrary map TestError }
}
