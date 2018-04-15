package http4s.extend.syntax

import cats.Eq

private[syntax] trait EqSyntax {
  implicit def eqSyntax(t: =>Throwable) = new ThrowableEqOps(t)
}

private[syntax] final class ThrowableEqOps(t: =>Throwable) {
  def ===(that: =>Throwable)(implicit te: Eq[Throwable]): Boolean =
    te.eqv(t, that)
}