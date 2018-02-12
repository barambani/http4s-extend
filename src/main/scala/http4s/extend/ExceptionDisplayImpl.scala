package http4s.extend

object ExceptionDisplayImpl extends NewType {
  def apply(b: String): T = b.asInstanceOf[T]
  def unwrap(t: T): String = t.asInstanceOf[String]
  def subst[F[_]](fs: F[String]): F[T] = fs.asInstanceOf[F[T]]
}