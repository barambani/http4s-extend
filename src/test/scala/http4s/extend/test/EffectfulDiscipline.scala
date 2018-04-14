package http4s.extend.test

import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.effect.laws.util.TestContext
import cats.instances.double._
import cats.instances.either._
import cats.instances.int._
import cats.instances.string._
import cats.instances.tuple._
import cats.instances.unit._
import cats.laws.discipline.MonadErrorTests
import http4s.extend.{ExceptionDisplay, _}
import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.test.laws.checks.EffectfulLawsChecks
import org.scalacheck.Arbitrary.arbDouble

final class EffectfulDiscipline extends MinimalSuite {

  implicit val C = TestContext()

  checkAll(
    "MonadError[IO, Throwable]",
    MonadErrorTests[IO, Throwable].monadError[String, Int, Double]
  )

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
}