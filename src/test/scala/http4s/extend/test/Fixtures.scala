package http4s.extend.test

import cats.Eq
import cats.effect.laws.util.TestInstances
import cats.tests.TestSettings
import http4s.extend.ExceptionDisplay
import http4s.extend.instances.eq._
import org.scalacheck.{Arbitrary, Cogen}
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

private[test] object Fixtures {

  final case class TestError(error: ExceptionDisplay)

  object TestError {

    implicit def testErrorArb(implicit A: Arbitrary[ExceptionDisplay]): Arbitrary[TestError] =
      Arbitrary { A.arbitrary map TestError.apply }

    implicit def testErrorCogen(implicit ev: Cogen[ExceptionDisplay]): Cogen[TestError] =
      ev contramap (_.error)

    implicit def testErrorEq: Eq[TestError] =
      Eq.by[TestError, ExceptionDisplay](_.error)
  }

  abstract class MinimalSuite
    extends FunSuite
    with    Matchers
    with    GeneratorDrivenPropertyChecks
    with    Discipline
    with    TestSettings
    with    TestInstances
}
