package http4s.extend.instances

import cats.Eq
import cats.instances.string._
import http4s.extend.ExceptionDisplay

trait EqInstances {

  implicit val throwableCompleteMessageEq: Eq[ExceptionDisplay] =
    Eq.by[ExceptionDisplay, String](ExceptionDisplay.unMk)
}
