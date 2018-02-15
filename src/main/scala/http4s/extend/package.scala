package http4s

package object extend {

  type ~~>[F[_], G[_]] = ByNameNt.~~>[F, G]

  val ExceptionDisplay = MkExceptionDisplay
  type ExceptionDisplay = ExceptionDisplay.T
}
