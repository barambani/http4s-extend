package http4s.extend.instances

import cats.Eq
import cats.instances.string._
import http4s.extend.Algebra.ThrowableCompleteMessage
import http4s.extend.util.ThrowableModule._

trait EqInstances {

  implicit def throwableCompleteMessageEq: Eq[ThrowableCompleteMessage] =
    Eq.by[ThrowableCompleteMessage, String](_.message)

  implicit def throwableEq(implicit ev: Eq[ThrowableCompleteMessage]): Eq[Throwable] =
    new Eq[Throwable]{
      def eqv(x: Throwable, y: Throwable): Boolean =
        ev.eqv(completeMessage(x), completeMessage(y))
    }
}