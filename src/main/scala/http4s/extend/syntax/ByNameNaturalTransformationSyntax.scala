package http4s.extend.syntax

import cats.MonadError
import http4s.extend.ByNameNaturalTransformation.~~>
import scala.language.higherKinds

trait ByNameNaturalTransformationSyntax {

  implicit final class ByNameNaturalTransformationOps[F[_], G[_], A](fa: F[A])(implicit nt: F ~~> G) {
    def lift: G[A] = nt(fa)
    def ~~>(): G[A] = lift
  }

  implicit final class ByNameNaturalTransformationOps2[F[_], G[_], A, E](fa: =>F[Either[E, A]])(implicit nt: F ~~> G) {
    def liftIntoMonadError(implicit err: MonadError[G, E]): G[A] =
      err.rethrow(fa.lift)
  }
}
