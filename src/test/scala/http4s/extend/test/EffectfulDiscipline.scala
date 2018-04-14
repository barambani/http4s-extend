package http4s.extend.test

import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.effect.laws.util.TestContext
import cats.instances.double._
import cats.instances.either._
import cats.instances.int._
import cats.instances.unit._
import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.test.laws.checks.EffectfulLawsChecks
import http4s.extend.{ExceptionDisplay, _}
import org.scalacheck.Arbitrary.arbDouble

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
}