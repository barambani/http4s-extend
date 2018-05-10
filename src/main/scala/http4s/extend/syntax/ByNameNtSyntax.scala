package http4s.extend.syntax

import cats.MonadError
import http4s.extend.ByNameNt.~~>

private[syntax] trait ByNameNtSyntax {

  implicit def byNameNtSyntax[F[_], A](fa: =>F[A]) = new ByNameNtOps(fa)

  implicit def byNameEitherNtSyntax[F[_], A, E](fa: =>F[Either[E, A]]) = new ByNameEitherNtOps(fa)
}

private[syntax] final class ByNameNtOps[F[_], A](fa: =>F[A]) {
  def transformTo[G[_]](implicit nt: F ~~> G): G[A] = nt.apply(fa)
  def ~~>[G[_] : F ~~> ?[_]]: G[A] = transformTo
}

private[syntax] final class ByNameEitherNtOps[F[_], A, E](val fa: F[Either[E, A]]) extends AnyVal {
  def liftIntoMonadError[G[_]](implicit nt: F ~~> G, err: MonadError[G, E]): G[A] =
    (err.rethrow[A] _ compose nt.apply)(fa)
}