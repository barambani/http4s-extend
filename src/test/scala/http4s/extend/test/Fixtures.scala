package http4s.extend.test

import cats.Eq
import http4s.extend.ErrorInvariantMap
import http4s.extend.Model.ThrowableCompleteMessage
import http4s.extend.test.Fixtures.TestError
import http4s.extend.test.laws.instances.EqInstances
import http4s.extend.util.ThrowableModule.{completeMessage, wrapCompleteMessage}
import org.scalacheck.{Arbitrary, Cogen}

trait Fixtures {

  def testErrorMap: ErrorInvariantMap[Throwable, TestError] =
    new ErrorInvariantMap[Throwable, TestError] {
      def direct: Throwable => TestError =
        th => TestError(completeMessage(th))

      def reverse: TestError => Throwable =
        er => wrapCompleteMessage(er.error)
    }
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
