package http4s.extend.test.laws.instances

import http4s.extend.{ExceptionDisplay, Void}
import monix.eval.{Task => MonixTask}
import org.scalacheck.{Arbitrary, Gen}
import scalaz.concurrent.{Task => ScalazTask}

private[test] trait ArbitraryInstances {

  implicit val arbVoid: Arbitrary[Void] =
    Arbitrary[Void] {
      Gen.const[Void](Void.mk(()))
    }

  implicit def throwableCompleteMessageArb(implicit A: Arbitrary[String]): Arbitrary[ExceptionDisplay] =
    Arbitrary { A.arbitrary map ExceptionDisplay.mk }

  implicit def scalazTaskArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[ScalazTask[A]] =
     Arbitrary { A.arbitrary map (ScalazTask.delay(_)) }

  implicit def monixTaskArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[MonixTask[A]] =
     Arbitrary { A.arbitrary map (MonixTask.delay(_)) }
}
