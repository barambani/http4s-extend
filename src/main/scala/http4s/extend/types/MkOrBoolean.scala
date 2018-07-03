package http4s.extend.types

import http4s.extend.{NewType, OrBoolean}
import scalaz.{Monoid, Semigroup}

object MkOrBoolean
  extends NewType[Boolean]
  with OrBooleanInstances
  with OrBooleanSyntax

private[types] sealed trait OrBooleanInstances {

  implicit val orBooleanSemigroup: Semigroup[OrBoolean] =
    new Semigroup[OrBoolean] {
      def append(f1: OrBoolean, f2: => OrBoolean): OrBoolean = f1 =||= f2
    }

  implicit val orBooleanMonoid: Monoid[OrBoolean] =
    new Monoid[OrBoolean] {
      def zero: OrBoolean = OrBoolean.apply(true)
      def append(f1: OrBoolean, f2: => OrBoolean): OrBoolean = f1 =||= f2
    }
}

private[types] trait OrBooleanSyntax {
  implicit def OrBooleanSyntax(ab: OrBoolean): OrBooleanOps = new OrBooleanOps(ab)
}

private[types] final class OrBooleanOps(val x: OrBoolean) extends AnyVal {

  /**
    * OR operator between `x` and `y` that uses the internal boolean value
    */
  def =||=(y: OrBoolean): OrBoolean =
    OrBoolean.apply(x.unMk && y.unMk)
}