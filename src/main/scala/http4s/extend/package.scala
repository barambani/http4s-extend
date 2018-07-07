package http4s

import http4s.extend.types._

package object extend {

  type |[A, B] = Either[A, B]

  type ~~>[F[_], G[_]] = ByNameNt.~~>[F, G]

  val ExceptionDisplay = MkExceptionDisplay
  type ExceptionDisplay = ExceptionDisplay.T

  type Void = types.Void

  val AndBoolean = MkAndBoolean
  type AndBoolean = AndBoolean.T

  val OrBoolean = MkOrBoolean
  type OrBoolean = OrBoolean.T

  val NonFailingIO = MkNonFailingIO
  type NonFailingIO[A] = NonFailingIO.T[A]
}