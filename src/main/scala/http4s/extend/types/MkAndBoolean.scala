package http4s.extend.types

import http4s.extend.{AndBoolean, newtype}
import scalaz.{Monoid, Semigroup}

object MkAndBoolean
  extends newtype[Boolean]
  with AndBooleanInstances
  with AndBooleanSyntax

private[types] sealed trait AndBooleanInstances {

  implicit val andBooleanSemigroup: Semigroup[AndBoolean] =
    new Semigroup[AndBoolean] {
      def append(f1: AndBoolean, f2: => AndBoolean): AndBoolean = f1 =&&= f2
    }

  implicit val andBooleanMonoid: Monoid[AndBoolean] =
    new Monoid[AndBoolean] {
      def zero: AndBoolean = AndBoolean(true)
      def append(f1: AndBoolean, f2: => AndBoolean): AndBoolean = f1 =&&= f2
    }
}

private[types] trait AndBooleanSyntax {
  implicit def andBooleanSyntax(ab: AndBoolean): AndBooleanOps = new AndBooleanOps(ab)
}

private[types] final class AndBooleanOps(val x: AndBoolean) extends AnyVal {

  /**
    * AND operator between `x` and `y` that uses the internal boolean value
    */
  def =&&=(y: AndBoolean): AndBoolean =
    AndBoolean(x.unMk && y.unMk)
}