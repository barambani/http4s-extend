package http4s.extend

trait ErrorInvariantMap[E1, E2] {
  def direct: E1 => E2
  def reverse: E2 => E1
}

object ErrorInvariantMap {
  @inline def apply[E1, E2](implicit E: ErrorInvariantMap[E1, E2]): ErrorInvariantMap[E1, E2] = E
}