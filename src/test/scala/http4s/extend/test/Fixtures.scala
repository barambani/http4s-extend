package http4s.extend.test

import cats.Eq
import http4s.extend.ExceptionDisplay
import http4s.extend.instances.eq._
import org.scalacheck.{Arbitrary, Cogen}

trait Fixtures

object Fixtures {

  case class TestError(error: ExceptionDisplay)

  object TestError {

    implicit def testErrorArb(implicit A: Arbitrary[ExceptionDisplay]): Arbitrary[TestError] =
      Arbitrary { A.arbitrary map TestError.apply }

    implicit def testErrorCogen(implicit ev: Cogen[ExceptionDisplay]): Cogen[TestError] =
      ev contramap (_.error)

    implicit def testErrorEq: Eq[TestError] =
      Eq.by[TestError, ExceptionDisplay](_.error)
  }
}
