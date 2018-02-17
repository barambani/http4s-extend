package http4s.extend.test

import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.laws.discipline.MonadErrorTests
import cats.tests.CatsSuite
import http4s.extend.instances.EqInstances
import http4s.extend.test.laws.checks.EffectfulLawsChecks
import org.scalacheck.Arbitrary.arbDouble

final class EffectfulDiscipline extends CatsSuite with EqInstances {

  checkAll(
    "MonadErrorTests[IO, Throwable]",
    MonadErrorTests[IO, Throwable].monadError[String, Int, Double]
  )

  checkAll(
    "EffectfulLawsChecks[IO]",
    EffectfulLawsChecks[IO].effectful[Double]
  )
}