package http4s.extend.test

import cats.{Eq, Invariant}
import http4s.extend.instances.eq._
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.syntax.invariant._
import http4s.extend.test.Fixtures.TestError
import http4s.extend.{ErrorInvariantMap, ExceptionDisplay}
import org.scalacheck.{Arbitrary, Cogen}

trait Fixtures {

  def testErrorMap(implicit ev: Invariant[ErrorInvariantMap[Throwable, ?]]): ErrorInvariantMap[Throwable, TestError] =
    ErrorInvariantMap[Throwable, ExceptionDisplay].imap(TestError)(_.error)
}

object Fixtures {

  case class TestError(error: ExceptionDisplay)

  object instances {

    implicit def testErrorArb(implicit A: Arbitrary[ExceptionDisplay]): Arbitrary[TestError] =
      Arbitrary { A.arbitrary map TestError }

    implicit def testErrorCogen(implicit ev: Cogen[ExceptionDisplay]): Cogen[TestError] =
      ev contramap (_.error)

    implicit def testErrorEq: Eq[TestError] =
      Eq.by[TestError, ExceptionDisplay](_.error)
  }
}
