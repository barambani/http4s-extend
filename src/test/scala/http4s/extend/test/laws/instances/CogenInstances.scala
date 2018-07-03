package http4s.extend.test.laws.instances

import http4s.extend.{ExceptionDisplay, Void}
import org.scalacheck.Cogen
import org.scalacheck.rng.Seed

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

private[test] trait CogenInstances {

  implicit def futureCogen[A : Cogen]: Cogen[Future[A]] =
    Cogen[Future[A]] { (seed: Seed, t: Future[A]) => Cogen[A].perturb(seed, Await.result(t, 1.second)) }

  implicit def exceptionDisplayCogen(implicit ev: Cogen[String]): Cogen[ExceptionDisplay] =
    ev contramap (_.unMk)

  implicit def voidCogen(implicit ev: Cogen[Unit]): Cogen[Void] =
    ev contramap ((_: Void) => ())
}
