package http4s.extend.test

import cats.{Eq, Invariant}
import http4s.extend.Algebra.ExceptionMessage
import http4s.extend.ErrorInvariantMap
import http4s.extend.instances.eq._
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.syntax.invariant._
import http4s.extend.test.Fixtures.TestError
import org.scalacheck.{Arbitrary, Cogen}

trait Fixtures {

  def testErrorMap(implicit ev: Invariant[ErrorInvariantMap[Throwable, ?]]): ErrorInvariantMap[Throwable, TestError] =
    ErrorInvariantMap[Throwable, ExceptionMessage].imap(TestError)(_.error)
}

object Fixtures {

  case class TestError(error: ExceptionMessage)

  object instances {

    implicit def testErrorArb(implicit A: Arbitrary[ExceptionMessage]): Arbitrary[TestError] =
      Arbitrary { A.arbitrary map TestError }

    implicit def testErrorCogen(implicit ev: Cogen[ExceptionMessage]): Cogen[TestError] =
      ev contramap (_.error)

    implicit def testErrorEq: Eq[TestError] =
      Eq.by[TestError, ExceptionMessage](_.error)
  }
}
