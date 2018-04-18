package http4s.extend.util

import http4s.extend.Iso

trait MonadErrorUtil {

  def scalazMonadError[F[_], E1, E2](implicit M: scalaz.MonadError[F, E1], I: Iso[E1, E2]): cats.MonadError[F, E2] =
    new cats.MonadError[F, E2] {

      def raiseError[A](e: E2): F[A] =
        (M.raiseError[A] _ compose I.from)(e)

      def handleErrorWith[A](fa: F[A])(f: E2 => F[A]): F[A] =
        M.handleError(fa)(f compose I.to)

      def pure[A](x: A): F[A] = M.pure(x)
      def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = M.bind(fa)(f)

      def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B] =
        M.bind(f(a)) {
          _.fold(tailRecM(_)(f), b => M.point(b))
        }
    }
}

object MonadErrorUtil extends MonadErrorUtil
