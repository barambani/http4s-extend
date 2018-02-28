package http4s.extend.syntax

import http4s.extend.ParEffectful

trait ParEffectfulSyntax {
  implicit def rarEffectfulSyntax2[F[_], A, B](t2: (F[A], F[B]))(implicit ev: ParEffectful[F]) = new Tuple2ParEffectfulOps(t2)
  implicit def rarEffectfulSyntax3[F[_], A, B, C](t3: (F[A], F[B], F[C]))(implicit ev: ParEffectful[F]) = new Tuple3ParEffectfulOps(t3)
}

final class Tuple2ParEffectfulOps[F[_], A, B](t2: (F[A], F[B]))(implicit ev: ParEffectful[F]) {

  def parMap[R](f: (A, B) => R): F[R] =
    ParEffectful.parMap2(t2._1, t2._2)(f)

  def parTupled: F[(A, B)] =
    ParEffectful.parTupled2(t2._1, t2._2)
}

final class Tuple3ParEffectfulOps[F[_], A, B, C](t3: (F[A], F[B], F[C]))(implicit ev: ParEffectful[F]) {

  def parMap[R](f: (A, B, C) => R): F[R] =
    ParEffectful.parMap3(t3._1, t3._2, t3._3)(f)

  def parTupled: F[(A, B, C)] =
    ParEffectful.parTupled3(t3._1, t3._2, t3._3)
}