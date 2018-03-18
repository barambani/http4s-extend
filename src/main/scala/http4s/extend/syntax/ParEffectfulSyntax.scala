package http4s.extend.syntax

import cats.{Applicative, Monoid, Traverse}
import http4s.extend.ParEffectful

trait ParEffectfulSyntax {
  implicit def parEffectfulSyntax2[F[_] : ParEffectful, A, B](t2: (F[A], F[B])) = new Tuple2ParEffectfulOps(t2)
  implicit def parEffectfulSyntax3[F[_] : ParEffectful, A, B, C](t3: (F[A], F[B], F[C])) = new Tuple3ParEffectfulOps(t3)

  implicit def parEffectfulTraverseSyntax[T[_] : Traverse : Applicative, A](t: T[A]) = new TraverseParEffectfulOps(t)
}

private[syntax] final class Tuple2ParEffectfulOps[F[_], A, B](t2: (F[A], F[B]))(implicit ev: ParEffectful[F]) {

  def parMap[R](f: (A, B) => R): F[R] =
    ParEffectful.parMap2(t2._1, t2._2)(f)

  def parTupled: F[(A, B)] =
    ParEffectful.parTupled2(t2._1, t2._2)
}

private[syntax] final class Tuple3ParEffectfulOps[F[_], A, B, C](t3: (F[A], F[B], F[C]))(implicit ev: ParEffectful[F]) {

  def parMap[R](f: (A, B, C) => R): F[R] =
    ParEffectful.parMap3(t3._1, t3._2, t3._3)(f)

  def parTupled: F[(A, B, C)] =
    ParEffectful.parTupled3(t3._1, t3._2, t3._3)
}

private[syntax] final class TraverseParEffectfulOps[T[_] : Traverse : Applicative, A](t: T[A]) {
  def parTraverse[F[_] : ParEffectful : Applicative, B](f: A => F[B])(implicit ev: Monoid[T[B]]): F[T[B]] =
    ParEffectful.parTraverse(t)(f)
}