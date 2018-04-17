package http4s.extend.syntax

import cats.MonadError
import http4s.extend.ByNameNt.~~>

private[syntax] trait ByNameNtSyntax {

  implicit def byNameNtSyntax[F[_], A](fa: =>F[A]): ByNameNtOps[F, A] = new ByNameNtOps(fa)

  implicit def byNameEitherNtSyntax[F[_], A, E](fa: =>F[Either[E, A]]): ByNameEitherNtOps[F, A, E] =
    new ByNameEitherNtOps(fa)
}

private[syntax] final class ByNameNtOps[F[_], A](fa: F[A]) {
  def to[G[_]](implicit nt: F ~~> G): G[A] = nt.apply(fa)
  def ~~>[G[_] : F ~~> ?[_]]: G[A] = to
}

private[syntax] final class ByNameEitherNtOps[F[_], A, E](fa: =>F[Either[E, A]]) {
  def liftIntoMonadError[G[_]](implicit nt: F ~~> G, err: MonadError[G, E]): G[A] =
    (err.rethrow[A] _ compose nt.apply)(fa)
}