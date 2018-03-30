package http4s.extend.test.laws.instances

import http4s.extend.ExceptionDisplay
import org.scalacheck.Cogen
import org.scalacheck.rng.Seed

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

private[test] trait CogenInstances {

  implicit def futureCogen[A : Cogen]: Cogen[Future[A]] =
    Cogen[Future[A]] { (seed: Seed, t: Future[A]) => Cogen[A].perturb(seed, Await.result(t, 1.second)) }

  implicit def throwableCompleteMessageCogen(implicit ev: Cogen[String]): Cogen[ExceptionDisplay] =
    ev contramap ExceptionDisplay.unMk
}
