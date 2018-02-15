package http4s.extend

object MkExceptionDisplay extends NewType {
  def mk(b: String): T = b.asInstanceOf[T]
  def unMk(t: T): String = t.asInstanceOf[String]
  def mkF[F[_]](fs: F[String]): F[T] = fs.asInstanceOf[F[T]]
}