package http4s.extend.syntax

import cats.MonadError
import http4s.extend.ByNameNt.~~>

private[syntax] trait ByNameNtSyntax {

  implicit def byNameNtSyntax[F[_], G[_], A](fa: =>F[A]): ByNameNtOps[F, G, A] = new ByNameNtOps(fa)

  implicit def byNameEitherNtSyntax[F[_], G[_], A, E](fa: =>F[Either[E, A]]): ByNameEitherNtOps[F, G, A, E] =
    new ByNameEitherNtOps(fa)
}

private[syntax] final class ByNameNtOps[F[_], G[_], A](fa: F[A]) {
  def transform(implicit nt: F ~~> G): G[A] = nt.apply(fa)
  def ~~>(implicit nt: F ~~> G): G[A] = transform
}

private[syntax] final class ByNameEitherNtOps[F[_], G[_], A, E](fa: =>F[Either[E, A]]) {
  def liftIntoMonadError(implicit nt: F ~~> G, err: MonadError[G, E]): G[A] =
    (err.rethrow[A] _ compose nt.apply)(fa)
}