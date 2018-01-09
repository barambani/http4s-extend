package http4s.extend

trait ErrorConversion[E1, E2] {
  def direct: E1 => E2
  def reverse: E2 => E1
}

object ErrorConversion {
  @inline def apply[E1, E2](implicit E: ErrorConversion[E1, E2]): ErrorConversion[E1, E2] = E
}