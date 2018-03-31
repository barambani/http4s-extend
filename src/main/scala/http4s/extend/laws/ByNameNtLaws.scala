package http4s.extend.laws

import cats.Functor
import cats.laws._
import cats.syntax.functor._
import http4s.extend.ByNameNt

sealed trait ByNameNtLaws[F[_], G[_]] {

  implicit def evF: Functor[F]
  implicit def evG: Functor[G]
  implicit def nt: ByNameNt[F, G]

  def commutativeDiagram[A, B](fa: =>F[A], f: A => B) = {
    nt.apply(fa map f) <-> (nt.apply(fa) map f)
  }
}