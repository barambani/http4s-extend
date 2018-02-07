package http4s.extend.test.laws.instances

import http4s.extend.Algebra.ExceptionMessage
import org.scalacheck.Arbitrary

trait ArbitraryInstances {

  implicit def throwableCompleteMessageArb(implicit A: Arbitrary[String]): Arbitrary[ExceptionMessage] =
    Arbitrary { A.arbitrary map (new ExceptionMessage(_)) }
}
