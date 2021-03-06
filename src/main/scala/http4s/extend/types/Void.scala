package http4s.extend.types

import cats.Eq
import http4s.extend.util.MonadErrorUtil
import http4s.extend.Iso

private[extend] sealed trait Void

private[types] object Void extends VoidCatsTypeclassInstances

private[types] sealed trait VoidCatsTypeclassInstances extends VoidScalazTypeclassInstances {

  implicit val voidEq: Eq[Void] =
    new Eq[Void] {
      def eqv(x: Void, y: Void): Boolean = true
    }

  implicit def monadError[F[_], E](implicit M: cats.MonadError[F, E], I: Iso[E, Void]): cats.MonadError[F, Void] =
    new cats.MonadError[F, Void] {

      def raiseError[A](e: Void): F[A] =
        (M.raiseError[A] _ compose I.from)(e)

      def handleErrorWith[A](fa: F[A])(f: Void => F[A]): F[A] =
        M.handleErrorWith(fa)(f compose I.to)

      def pure[A](x: A): F[A] = M.pure(x)
      def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = M.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B] = M.tailRecM(a)(f)
    }
}

private[types] sealed trait VoidScalazTypeclassInstances extends MonadErrorUtil {

  implicit def voidMonadError[F[_], E](implicit M: scalaz.MonadError[F, E], I: Iso[E, Void]): cats.MonadError[F, Void] =
    scalazMonadError[F, E, Void]
}