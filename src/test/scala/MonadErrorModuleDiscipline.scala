import java.util.concurrent.ForkJoinPool

import cats.laws.discipline.MonadErrorTests
import cats.tests.CatsSuite
import cats.{Eq, MonadError}
import http4s.extend.instances.errorInvariantMap._
import http4s.extend.syntax.monadError._
import http4s.extend.{ErrorAdapt, ErrorInvariantMap}
import laws.checks.ErrorInvariantMapLawsChecks
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Cogen}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

final class MonadErrorModuleDiscipline extends CatsSuite {

  implicit val futureExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(new ForkJoinPool())

  case class TestError(error: String)

  def futureEither[A](fa: Future[A]): Future[Either[Throwable, A]] =
    ErrorAdapt[Future].attemptMapLeft(fa)(identity[Throwable])

  /**
    * Equality instances
    */
  implicit val throwableEq: Eq[Throwable] =
    Eq.by[Throwable, String](_.getMessage)

  implicit val testErrorEq: Eq[TestError] =
    Eq.by[TestError, String](_.error)

  implicit def equalFuture[A: Eq]: Eq[Future[A]] =
    (fx: Future[A], fy: Future[A]) =>
      Await.result(futureEither(fx) zip futureEither(fy) map { case (tx, ty) => tx === ty }, 1.second)

  /**
    * Cogen instances
    */
  implicit def futureCogen[A : Cogen]: Cogen[Future[A]] =
    Cogen[Future[A]] { (seed: Seed, t: Future[A]) => Cogen[A].perturb(seed, Await.result(t, 1.second)) }

  implicit def testErrorCogen(implicit IC: Cogen[String]): Cogen[TestError] =
    IC contramap (_.error)

  /**
    * Arbitrary instances
    */
  implicit def testErrorArb(implicit AI: Arbitrary[String]): Arbitrary[TestError] =
    Arbitrary { AI.arbitrary map TestError }

  /**
    * ErrorInvariantMap under test
    */
  implicit def testErrorMap: ErrorInvariantMap[Throwable, TestError] =
    new ErrorInvariantMap[Throwable, TestError] {
      def direct: Throwable => TestError =
        th => TestError(th.getMessage)

      def reverse: TestError => Throwable =
        te => new Throwable(te.error)
    }

  /**
    * MonadError under test
    */
  val testErrorInvariantMap: ErrorInvariantMap[Throwable, TestError] =
    new ErrorInvariantMap[Throwable, TestError] {
      def direct: Throwable => TestError =
        th => TestError(th.getMessage)

      def reverse: TestError => Throwable =
        er => new Throwable(er.error)
    }

  val stringAdapt: MonadError[Future, String] =
    MonadError[Future, Throwable].adaptErrorType[String]

  val testErrorAdapt: MonadError[Future, TestError] =
    MonadError[Future, Throwable].adaptErrorType[TestError](testErrorInvariantMap)

  /**
    * Verification
    */
  checkAll("ErrorInvariantMapLawsChecks[Throwable, String]", ErrorInvariantMapLawsChecks[Throwable, String].errorInvariantMap)
  checkAll("ErrorInvariantMapLawsChecks[Throwable, TestError]", ErrorInvariantMapLawsChecks[Throwable, TestError].errorInvariantMap)

  checkAll("MonadErrorTests[Future, String]", MonadErrorTests[Future, String](stringAdapt).monadError[String, Boolean, Int])
  checkAll("MonadErrorTests[Future, TestError]", MonadErrorTests[Future, TestError](testErrorAdapt).monadError[Boolean, Int, String])
}