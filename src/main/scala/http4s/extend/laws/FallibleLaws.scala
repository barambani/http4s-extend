package http4s.extend.laws

import cats.laws._
import http4s.extend.Fallible

trait FallibleLaws[E, F[_], G[_]] {

  implicit def fa: Fallible[E, F, G]

  def flatMapNotProgressOnFail[A](e: E, f: A => F[A]) =
    fa.M.flatMap(fa.raiseError(e))(f) <-> fa.raiseError[A](e)
}
