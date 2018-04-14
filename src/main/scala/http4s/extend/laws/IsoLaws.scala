package http4s.extend.laws

import cats.laws._
import http4s.extend.Iso

sealed trait IsoLaws[A, B] {

  implicit def iso: Iso[A, B]

  def leftIdentity(a: A) =
    (iso.from compose iso.to)(a) <-> a

  def rightIdentity(b: B) =
    (iso.to compose iso.from)(b) <-> b
}

object IsoLaws {

  @inline def apply[A, B](implicit ev: Iso[A, B]): IsoLaws[A, B] =
    new IsoLaws[A, B] {
      val iso: Iso[A, B] = ev
    }
}