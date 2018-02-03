package http4s.extend.test

import cats.{Eq, Invariant}
import http4s.extend.Algebra.ThrowableCompleteMessage
import http4s.extend.ErrorInvariantMap
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.syntax.invariant._
import http4s.extend.test.Fixtures.TestError
import http4s.extend.test.laws.instances.EqInstances
import org.scalacheck.{Arbitrary, Cogen}

trait Fixtures {

  def testErrorMap(implicit ev: Invariant[ErrorInvariantMap[Throwable, ?]]): ErrorInvariantMap[Throwable, TestError] =
    ErrorInvariantMap[Throwable, ThrowableCompleteMessage].imap(TestError)(_.error)
}

object Fixtures {

  case class TestError(error: ThrowableCompleteMessage)

  object instances extends EqInstances {

    implicit def testErrorArb(implicit A: Arbitrary[ThrowableCompleteMessage]): Arbitrary[TestError] =
      Arbitrary { A.arbitrary map TestError }

    implicit def testErrorCogen(implicit ev: Cogen[ThrowableCompleteMessage]): Cogen[TestError] =
      ev contramap (_.error)

    implicit def testErrorEq: Eq[TestError] =
      Eq.by[TestError, ThrowableCompleteMessage](_.error)
  }
}
