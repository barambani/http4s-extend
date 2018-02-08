package http4s.extend

object ExceptionDisplayType {

  sealed abstract class ExceptionDisplayImpl {
    type T
    def apply(s: String): T
    def unwrap(lbl: T): String
    def subst[F[_]](value: F[String]): F[T]
  }

  val ExceptionDisplay: ExceptionDisplayImpl =
    new ExceptionDisplayImpl {
      type T = String
      override def apply(s: String) = s
      override def unwrap(lbl: T) = lbl
      override def subst[F[_]](value: F[String]): F[T] =
        value.asInstanceOf[F[T]]
    }

  type ExceptionDisplay = ExceptionDisplay.T
}