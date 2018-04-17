package http4s.extend.laws

import cats.Semigroup
import cats.laws._
import cats.syntax.semigroup._
import http4s.extend.{Effectful, ParEffectful}

sealed trait ParEffectfulLaws[E, F[_]] {

  implicit def sem: Semigroup[E]
  implicit def eff: Effectful[E, F]
  implicit def pEff: ParEffectful[F]

  def errorAccumulate[A](e1: E, e2: E) =
    pEff.parMap2(eff.fail[A](e1), eff.fail[A](e2)){ (_, _) => () } <-> eff.fail[Unit](e1 combine e2)
}

object ParEffectfulLaws {
  @inline def apply[F[_], E](
    implicit
      ev1: ParEffectful[F],
      ev2: Effectful[E, F],
      ev3: Semigroup[E]): ParEffectfulLaws[E, F] =
    new ParEffectfulLaws[E, F] {
      val pEff: ParEffectful[F] = ev1
      val eff: Effectful[E, F] = ev2
      val sem: Semigroup[E] = ev3
    }
}
