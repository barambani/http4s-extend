package http4s.extend.test

import cats.MonadError
import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.laws.discipline.MonadErrorTests
import cats.tests.CatsSuite
import http4s.extend.ExceptionDisplay
import http4s.extend.instances.eq._
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.instances.invariant._
import http4s.extend.syntax.monadError._
import http4s.extend.test.Fixtures.TestError
import http4s.extend.test.Fixtures.instances._
import http4s.extend.test.laws.checks.ErrorInvariantMapLawsChecks
import http4s.extend.test.laws.instances.{ArbitraryInstances, CogenInstances}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class MonadErrorModuleDiscipline extends CatsSuite with CogenInstances with ArbitraryInstances with Fixtures {

  /**
    * MonadError under test
    */
  def futureMonadErrorWithString: MonadError[Future, ExceptionDisplay] =
    MonadError[Future, Throwable].adaptErrorType[ExceptionDisplay]

  def futureMonadError: MonadError[Future, TestError] =
    MonadError[Future, Throwable].adaptErrorType[TestError](testErrorMap)

  def ioMonadError: MonadError[IO, TestError] =
    MonadError[IO, Throwable].adaptErrorType[TestError](testErrorMap)

  /**
    * Error map verification
    */
  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, ThrowableCompleteMessage]",
    ErrorInvariantMapLawsChecks[Throwable, ExceptionDisplay].errorInvariantMap
  )

  checkAll(
    "ErrorInvariantMapLawsChecks[Throwable, TestError]",
    ErrorInvariantMapLawsChecks[Throwable, TestError](testErrorMap).errorInvariantMap
  )

  /**
    * MonadError verification
    */
  checkAll(
    "MonadErrorTests[Future, ThrowableCompleteMessage]",
    MonadErrorTests[Future, ExceptionDisplay](futureMonadErrorWithString).monadError[String, Int, Double]
  )

  checkAll(
    "MonadErrorTests[Future, TestError]",
    MonadErrorTests[Future, TestError](futureMonadError).monadError[Double, Int, String]
  )

  checkAll(
    "MonadErrorTests[IO, TestError]",
    MonadErrorTests[IO, TestError](ioMonadError).monadError[Double, Int, String]
  )
}