package http4s

import http4s.extend.types.{MkAndBoolean, MkExceptionDisplay, MkOrBoolean, MkVoid}

package object extend {

  type ~~>[F[_], G[_]] = ByNameNt.~~>[F, G]

  val ExceptionDisplay = MkExceptionDisplay
  type ExceptionDisplay = ExceptionDisplay.T

  val Void = MkVoid
  type Void = Void.T

  val AndBoolean = MkAndBoolean
  type AndBoolean = AndBoolean.T

  val OrBoolean = MkOrBoolean
  type OrBoolean = OrBoolean.T
}
