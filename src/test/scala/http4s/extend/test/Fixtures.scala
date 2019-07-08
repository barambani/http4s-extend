package http4s.extend.test

import cats.{Eq, Semigroup}
import cats.effect.IO
import cats.effect.laws.util.{TestContext, TestInstances}
import cats.effect.util.CompositeException
import cats.tests.TestSettings
import http4s.extend.{Effectful, ExceptionDisplay}
import http4s.extend.test.laws.instances.{ArbitraryInstances, CogenInstances, EqTestInstances}
import org.scalacheck.{Arbitrary, Cogen}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
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
    with    ArbitraryInstances
    with    CogenInstances
    with    EqTestInstances {

    implicit val C = TestContext()
    implicit val timer        = IO.timer(C)
    implicit val contextShift = IO.contextShift(C)

    implicit def throwableSemigroup: Semigroup[Throwable] =
      new Semigroup[Throwable]{
        def combine(x: Throwable, y: Throwable): Throwable =
          CompositeException(x, y, Nil)
      }

    def ioEff = Effectful[Throwable, IO]
  }
}
