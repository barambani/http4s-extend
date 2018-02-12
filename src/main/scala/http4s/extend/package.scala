package http4s

package object extend {

  type ~~>[F[_], G[_]] = ByNameNt.~~>[F, G]

  type ExceptionDisplay = ExceptionDisplay.T
  val ExceptionDisplay = ExceptionDisplayImpl
}
