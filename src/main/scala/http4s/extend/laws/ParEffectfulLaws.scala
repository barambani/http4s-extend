package http4s.extend.laws

import cats.Semigroup
import cats.laws._
import cats.syntax.semigroup._
import http4s.extend.{Effectful, ParEffectful}

sealed trait ParEffectfulLaws[F[_]] {

  implicit def sem[E]: Semigroup[E]
  implicit def eff[E]: Effectful[E, F]
  implicit def pEff: ParEffectful[F]

  def errorAccumulate[A, E](e1: E, e2: E) =
    pEff.parMap2(eff.fail[A](e1), eff.fail[A](e2)){ (_, _) => () } <-> eff.fail[Unit](e1 combine e2)
}
