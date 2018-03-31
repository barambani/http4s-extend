package http4s.extend.laws

import cats.Functor
import cats.laws._
import cats.syntax.functor._
import http4s.extend.ByNameNt
import http4s.extend.ByNameNt.~~>

sealed trait ByNameNtLaws[F[_], G[_]] {

  implicit def evF: Functor[F]
  implicit def evG: Functor[G]
  implicit def nt: ByNameNt[F, G]

  /**
    * This law is automatically satisfied as a consequence of Parametricity.
    * Here is implemented as an exercise
    */
  def naturalityCondition[A, B](fa: F[A], f: A => B) =
    nt.apply(fa map f) <-> (nt.apply(fa) map f)
}

object ByNameNtLaws {

  @inline def apply[F[_], G[_]](implicit ev: F ~~> G, F: Functor[F], G: Functor[G]): ByNameNtLaws[F, G] =
    new ByNameNtLaws[F, G] {
      val evF: Functor[F] = F
      val evG: Functor[G] = G
      val nt: ByNameNt[F, G] = ev
    }
}