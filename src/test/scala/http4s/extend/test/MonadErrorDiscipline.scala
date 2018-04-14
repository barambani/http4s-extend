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
import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.{ExceptionDisplay, Void}
import org.scalacheck.Arbitrary.arbDouble

final class MonadErrorDiscipline extends MinimalSuite {

  implicit val C = TestContext()

  checkAll(
    "MonadError[IO, ExceptionDisplay]",
    MonadErrorTests[IO, ExceptionDisplay].monadError[String, Int, Double]
  )

  checkAll(
    "MonadError[IO, Throwable]",
    MonadErrorTests[IO, Throwable].monadError[String, Int, Double]
  )

  checkAll(
    "MonadError[IO, Void]",
    MonadErrorTests[IO, Void].monadError[String, Int, Double]
  )

}
