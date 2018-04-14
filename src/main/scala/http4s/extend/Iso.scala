package http4s.extend

import shapeless.Lazy

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

  implicit def isoSymmetric[A, B](implicit I: Lazy[Iso[A, B]]): Iso[B, A] =
    new Iso[B, A] {
      def to: B => A = I.value.from
      def from: A => B = I.value.to
    }
}

object Iso extends IsoInstances {
  @inline def apply[A, B](implicit F: Iso[A, B]): Iso[A, B] = F
}