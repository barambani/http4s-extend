package http4s.extend.test.laws.implicits

import http4s.extend.test.Fixtures
import org.scalacheck.Cogen
import org.scalacheck.rng.Seed

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait CogenInstances extends Fixtures {

  implicit def futureCogen[A : Cogen]: Cogen[Future[A]] =
    Cogen[Future[A]] { (seed: Seed, t: Future[A]) => Cogen[A].perturb(seed, Await.result(t, 1.second)) }

  implicit def testErrorCogen(implicit SC: Cogen[String]): Cogen[TestError] =
    SC contramap (_.error)
}
