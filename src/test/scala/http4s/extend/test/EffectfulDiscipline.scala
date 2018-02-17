package http4s.extend.test

import java.util.concurrent.ForkJoinPool

import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.laws.discipline.MonadErrorTests
import cats.tests.CatsSuite
import http4s.extend.instances.EqInstances
import http4s.extend.test.laws.checks.EffectfulLawsChecks
import org.scalacheck.Arbitrary.arbDouble

import scala.concurrent.ExecutionContext

final class EffectfulDiscipline extends CatsSuite with EqInstances {

  implicit val effectExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(new ForkJoinPool())

  checkAll(
    "MonadErrorTests[Future, Throwable]",
    MonadErrorTests[IO, Throwable].monadError[String, Int, Double]
  )

  checkAll(
    "EffectfulLawsChecks[IO]",
    EffectfulLawsChecks[IO].effectful[Double]
  )
}