package http4s.extend.syntax

import cats.MonadError
import http4s.extend.ByNameNt.~~>

trait ByNameNtSyntax {

  implicit def byNameNtSyntax[F[_] : ?[_] ~~> G, G[_], A](fa: F[A]): ByNameNtOps[F, G, A] = new ByNameNtOps(fa)

  implicit def byNameEitherNtSyntax[F[_] : ?[_] ~~> G, G[_], A, E](fa: =>F[Either[E, A]]): ByNameEitherNtOps[F, G, A, E] =
    new ByNameEitherNtOps(fa)
}

final class ByNameNtOps[F[_], G[_], A](fa: F[A])(implicit nt: F ~~> G) {
  def lift: G[A] = nt(fa)
  def ~~>(): G[A] = lift
}

final class ByNameEitherNtOps[F[_], G[_], A, E](fa: =>F[Either[E, A]])(implicit nt: F ~~> G) {
  def liftIntoMonadError(implicit err: MonadError[G, E]): G[A] =
    (err.rethrow[A] _ compose nt.apply)(fa)
}