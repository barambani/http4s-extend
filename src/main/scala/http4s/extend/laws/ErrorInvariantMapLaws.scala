package http4s.extend.laws

import cats.laws._
import http4s.extend.ErrorInvariantMap

sealed trait ErrorInvariantMapLaws[E1, E2] {

  implicit def F: ErrorInvariantMap[E1, E2]

  def directIdentity(e1: E1): IsEq[E1] =
    (F.reverse compose F.direct)(e1) <-> e1

  def reverseIdentity(e2: E2): IsEq[E2] =
    (F.direct compose F.reverse)(e2) <-> e2
}

object ErrorInvariantMapLaws {

  @inline def apply[E1, E2](implicit ev: ErrorInvariantMap[E1, E2]): ErrorInvariantMapLaws[E1, E2] =
    new ErrorInvariantMapLaws[E1, E2] {
      def F: ErrorInvariantMap[E1, E2] = ev
    }
}