package http4s.extend

trait NewType[R] {
  type Base <: Any
  private [extend] trait Tag extends Any
  type T <: Base with Tag

  def apply(a: R): T = a.asInstanceOf[T]
  def mkF[F[_]](fs: F[R]): F[T] = fs.asInstanceOf[F[T]]
}

object NewType {
  implicit private[extend] final class NewTypeSyntax[R](private val t: NewType[R]#T) extends AnyVal {
    def unMk: R = t.asInstanceOf[R]
  }
}