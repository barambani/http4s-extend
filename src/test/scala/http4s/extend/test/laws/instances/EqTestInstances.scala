package http4s.extend.test.laws.instances

import cats.Eq
import cats.effect.IO
import http4s.extend.syntax.byNameNt._
import scalaz.concurrent.{Task => ScalazTask}

private[test] trait EqTestInstances {

  implicit def taskDoubleEq[A](implicit ev: Eq[IO[A]]): Eq[ScalazTask[A]] =
    new Eq[ScalazTask[A]] {
      def eqv(x: ScalazTask[A], y: ScalazTask[A]): Boolean =
        ev.eqv(x.transformTo[IO], y.transformTo[IO])
    }
}
