package http4s.extend.test.laws.instances

import http4s.extend.ExceptionDisplayType._
import org.scalacheck.Arbitrary

trait ArbitraryInstances {

  implicit def throwableCompleteMessageArb(implicit A: Arbitrary[String]): Arbitrary[ExceptionDisplay] =
    Arbitrary { A.arbitrary map ExceptionDisplay.apply }
}
