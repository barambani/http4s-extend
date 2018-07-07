package http4s.extend.test.laws.instances

import http4s.extend.ExceptionDisplay
import monix.eval.{Task => MonixTask}
import org.scalacheck.Arbitrary
import scalaz.concurrent.{Task => ScalazTask}


private[test] trait ArbitraryInstances {

  implicit def throwableCompleteMessageArb(implicit A: Arbitrary[String]): Arbitrary[ExceptionDisplay] =
    Arbitrary { A.arbitrary map ExceptionDisplay.apply }

  implicit def scalazTaskArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[ScalazTask[A]] =
     Arbitrary { A.arbitrary map (ScalazTask.delay(_)) }

  implicit def monixTaskArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[MonixTask[A]] =
     Arbitrary { A.arbitrary map (MonixTask.delay(_)) }
}
