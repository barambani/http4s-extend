package http4s.extend.syntax

import cats.Eq

trait EqSyntax {
  implicit def eqSyntax(t: => Throwable)(implicit te: Eq[Throwable]) = new ThrowableEqOps(t)
}

final class ThrowableEqOps(t: => Throwable)(implicit te: Eq[Throwable]) {
  def ===(that: => Throwable): Boolean =
    te.eqv(t, that)
}