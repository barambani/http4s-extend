package http4s.extend.test

import cats.MonadError
import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.effect.laws.util.TestContext
import cats.instances.double._
import cats.instances.string._
import cats.instances.either._
import cats.instances.int._
import cats.instances.unit._
import cats.laws._
import cats.laws.discipline._
import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.test.laws.checks.EffectfulLawsChecks
import http4s.extend.{ExceptionDisplay, _}
import org.scalacheck.Arbitrary.arbDouble
import org.scalacheck.Prop

final class EffectfulDiscipline extends MinimalSuite {

  implicit val C = TestContext()

  checkAll(
    "Effectful[ExceptionDisplay, IO]",
    EffectfulLawsChecks[ExceptionDisplay, IO].effectful[Double]
  )

  checkAll(
    "Effectful[Throwable, IO]",
    EffectfulLawsChecks[Throwable, IO].effectful[Int]
  )

  checkAll(
    "Effectful[Void, IO]",
    EffectfulLawsChecks[Void, IO].effectful[Int]
  )

  test("Effectful[Throwable, IO]: throw a Throwable in delay is raiseError"){
    check(Prop.forAll {
      e: Throwable =>
        Effectful[Throwable, IO].delay[Int](throw e) <-> MonadError[IO, Throwable].raiseError[Int](e)
    })
  }

  test("Effectful[Throwable, IO]: throw a Throwable in suspend is raiseError"){
    check(Prop.forAll {
      e: Throwable =>
        Effectful[Throwable, IO].suspend[String](throw e) <-> MonadError[IO, Throwable].raiseError[String](e)
    })
  }

  test("Effectful[Throwable, IO]: propagate errors through bind (suspend)"){
    check(Prop.forAll {
      e: Throwable => {

        val monadError = MonadError[IO, Throwable]
        val fa = monadError.flatMap(Effectful[Throwable, IO].delay[Double](throw e))(x => monadError.pure[Double](x))

        fa <-> monadError.raiseError[Double](e)
      }
    })
  }
}