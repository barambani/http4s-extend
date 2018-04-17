package http4s.extend

trait Iso[A, B] {
  def to: A => B
  def from: B => A
}

private[extend] sealed trait IsoInstances {

  implicit def isoReflexive[A]: Iso[A, A] =
    new Iso[A, A] {
      def to: A => A = identity[A]
      def from: A => A = identity[A]
    }

  implicit def isoSymmetric[A, B](implicit I: Iso[A, B]): Iso[B, A] =
    new Iso[B, A] {
      def to: B => A = I.from
      def from: A => B = I.to
    }
}

object Iso extends IsoInstances {
  @inline def apply[A, B](implicit F: Iso[A, B]): Iso[A, B] = F
}