package http4s.extend

trait NewTypeF[F[_]] {
  type Base <: Any
  private [extend] trait Tag extends Any
  type T[_] <: Base with Tag

  def apply[A](fa: F[A]): T[A] = fa.asInstanceOf[T[A]]
}