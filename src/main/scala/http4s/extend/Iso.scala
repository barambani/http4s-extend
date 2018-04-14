package http4s.extend

trait Iso[A, B] {
  def to: A => B
  def from: B => A
}

private[extend] sealed trait IsoInstances {

  implicit def isoReflexive[A]: Iso[A, A] =
    new Iso[A, A] {
      def to: A => A = identity
      def from: A => A = identity
    }

//  implicit def isoSymmetric[A, B](implicit I: Iso[A, B]): Iso[B, A] =
//    new Iso[B, A] {
//      def to: B => A = I.from
//      def from: A => B = I.to
//    }

  implicit def isoTransitive[A, B, C](implicit I1: Iso[A, B], I2: Iso[B, C]): Iso[A, C] =
    new Iso[A, C] {
      def to: A => C = I2.to compose I1.to
      def from: C => A = I1.from compose I2.from
    }
}

object Iso extends IsoInstances {
  @inline def apply[A, B](implicit F: Iso[A, B]): Iso[A, B] = F
}