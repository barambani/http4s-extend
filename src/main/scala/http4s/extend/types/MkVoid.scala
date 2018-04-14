package http4s.extend.types

import cats.{Eq, MonadError}
import http4s.extend.{Iso, NewType, Void}

object MkVoid extends NewType with VoidTypeclassInstances {
  def mk(b: Unit): T = b.asInstanceOf[T]
  def unMk(t: T): Unit = t.asInstanceOf[Unit]
  def mkF[F[_]](fs: F[Unit]): F[T] = fs.asInstanceOf[F[T]]
}

private[types] sealed trait VoidTypeclassInstances {

  implicit val voidEq: Eq[Void] =
    new Eq[Void] {
      def eqv(x: Void, y: Void): Boolean = true
    }

  implicit val isoThrowable: Iso[Throwable, Void] =
    new Iso[Throwable, Void] {
      def to: Throwable => Void = _ => Void.mk(())
      def from: Void => Throwable = _ =>  new Exception("Undefined")
    }

  implicit def monadError[F[_], E](implicit M: MonadError[F, E], I: Iso[E, Void]): MonadError[F, Void] =
    new MonadError[F, Void] {

      def raiseError[A](e: Void): F[A] =
        (M.raiseError[A] _ compose I.from)(e)

      def handleErrorWith[A](fa: F[A])(f: Void => F[A]): F[A] =
        M.handleErrorWith(fa)(f compose I.to)

      def pure[A](x: A): F[A] = M.pure(x)
      def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = M.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B] = M.tailRecM(a)(f)
    }
}