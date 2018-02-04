package http4s.extend.test.laws.instances

import http4s.extend.Algebra.ThrowableCompleteMessage
import org.scalacheck.Cogen

trait CogenInstances {

  implicit def throwableCompleteMessageCogen(implicit ev: Cogen[String]): Cogen[ThrowableCompleteMessage] =
    ev contramap (_.message)
}
