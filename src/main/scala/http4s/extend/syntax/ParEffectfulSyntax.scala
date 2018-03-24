package http4s.extend.syntax

import cats.{Applicative, Monoid, Traverse}
import http4s.extend.ParEffectful

private[syntax] trait ParEffectfulSyntax extends ParEffectfulAritySyntax {
  implicit def parEffectfulSyntax2[F[_] : ParEffectful, A, B](t2: (F[A], F[B])) = new Tuple2ParEffectfulOps(t2)
  implicit def parEffectfulSyntax3[F[_] : ParEffectful, A, B, C](t3: (F[A], F[B], F[C])) = new Tuple3ParEffectfulOps(t3)

  implicit def parEffectfulTraverseSyntax[T[_] : Traverse : Applicative, A](t: T[A]) = new TraverseParEffectfulOps(t)
}

private[syntax] final class Tuple2ParEffectfulOps[F[_] : ParEffectful, A1, A2](t2: (F[A1], F[A2])) {
  def parMap[R](f: (A1, A2) => R): F[R] = ParEffectful.parMap2(t2._1, t2._2)(f)
  def parTupled: F[(A1, A2)] = ParEffectful.parTupled2(t2._1, t2._2)
}

private[syntax] final class Tuple3ParEffectfulOps[F[_] : ParEffectful, A1, A2, A3](t3: (F[A1], F[A2], F[A3])) {
  def parMap[R](f: (A1, A2, A3) => R): F[R] = ParEffectful.parMap3(t3._1, t3._2, t3._3)(f)
  def parTupled: F[(A1, A2, A3)] = ParEffectful.parTupled3(t3._1, t3._2, t3._3)
}

private[syntax] final class TraverseParEffectfulOps[T[_] : Traverse : Applicative, A](t: T[A]) {
  def parTraverse[F[_] : ParEffectful : Applicative, B](f: A => F[B])(implicit ev: Monoid[T[B]]): F[T[B]] =
    ParEffectful.parTraverse(t)(f)
}