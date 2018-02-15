package http4s.extend.test.laws.instances

import http4s.extend.ExceptionDisplay
import org.scalacheck.Arbitrary

trait ArbitraryInstances {

  implicit def throwableCompleteMessageArb(implicit A: Arbitrary[String]): Arbitrary[ExceptionDisplay] =
    Arbitrary { A.arbitrary map ExceptionDisplay.mk }
}
