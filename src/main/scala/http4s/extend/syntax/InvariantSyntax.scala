package http4s.extend.syntax

import cats.Invariant
import http4s.extend.ErrorInvariantMap

trait InvariantSyntax {
  implicit def invariantSyntax[E1, E2](fa: ErrorInvariantMap[E1, E2])(implicit ev: Invariant[ErrorInvariantMap[E1, ?]]) =
    new ErrorInvariantMapOps(fa)
}

final class ErrorInvariantMapOps[E1, E2](fa: ErrorInvariantMap[E1, E2])(implicit ev: Invariant[ErrorInvariantMap[E1, ?]]) {
  def imap[E3](f: E2 => E3)(g: E3 => E2): ErrorInvariantMap[E1, E3] = ev.imap(fa)(f)(g)
}