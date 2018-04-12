package http4s

import http4s.extend.types.{MkExceptionDisplay, MkVoid}

package object extend {

  type ~~>[F[_], G[_]] = ByNameNt.~~>[F, G]

  val ExceptionDisplay = MkExceptionDisplay
  type ExceptionDisplay = ExceptionDisplay.T

  val Void = MkVoid
  type Void = Void.T
}
