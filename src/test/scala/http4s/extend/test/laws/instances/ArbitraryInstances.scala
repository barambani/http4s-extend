package http4s.extend.test.laws.instances

import http4s.extend.Model.ThrowableCompleteMessage
import org.scalacheck.Arbitrary

trait ArbitraryInstances {

  implicit def throwableCompleteMessageArb(implicit A: Arbitrary[String]): Arbitrary[ThrowableCompleteMessage] =
    Arbitrary { A.arbitrary map (new ThrowableCompleteMessage(_)) }
}
