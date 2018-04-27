package http4s.extend.types

import cats.Eq
import cats.instances.string._
import http4s.extend.util.MonadErrorUtil
import http4s.extend.{ExceptionDisplay, Iso, NewType}

object MkExceptionDisplay extends NewType with ExceptionDisplayCatsTypeclassInstances with ExceptionDisplayFunctions {

  def apply(b: String): T = b.asInstanceOf[T]
  def mkF[F[_]](fs: F[String]): F[T] = fs.asInstanceOf[F[T]]

  implicit final class MkExceptionDisplaySyntax(val t: T) extends AnyVal {
    def unMk: String = t.asInstanceOf[String]
  }
}

private[types] sealed trait ExceptionDisplayCatsTypeclassInstances {

  implicit val exceptionDisplayEq: Eq[ExceptionDisplay] =
    Eq.by[ExceptionDisplay, String](_.unMk)

  implicit val isoThrowable: Iso[Throwable, ExceptionDisplay] =
    new Iso[Throwable, ExceptionDisplay] {

      def to: Throwable => ExceptionDisplay =
        ExceptionDisplay.fullDisplay

      def from: ExceptionDisplay => Throwable =
        ExceptionDisplay.throwableOf
    }

  implicit def monadError[F[_], E](implicit M: cats.MonadError[F, E], I: Iso[E, ExceptionDisplay]): cats.MonadError[F, ExceptionDisplay] =
    new cats.MonadError[F, ExceptionDisplay] {

      def raiseError[A](e: ExceptionDisplay): F[A] =
        (M.raiseError[A] _ compose I.from)(e)

      def handleErrorWith[A](fa: F[A])(f: ExceptionDisplay => F[A]): F[A] =
        M.handleErrorWith(fa)(f compose I.to)

      def pure[A](x: A): F[A] = M.pure(x)
      def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = M.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B] = M.tailRecM(a)(f)
    }
}

private[types] sealed trait ExceptionDisplayScalazTypeclassInstances extends MonadErrorUtil {

  implicit def exceptionDisplayMonadError[F[_], E](implicit M: scalaz.MonadError[F, E], I: Iso[E, ExceptionDisplay]): cats.MonadError[F, ExceptionDisplay] =
    scalazMonadError[F, E, ExceptionDisplay]
}

private[types] sealed trait ExceptionDisplayFunctions {

  private val separator = "\n\rcaused by "

  def throwableOf: ExceptionDisplay => Throwable =
    xs => throwableHierarchy {
      xs.unMk.split(separator).toSeq map ExceptionDisplay.apply
    }

  def throwableHierarchy: Seq[ExceptionDisplay] => Throwable =
    xs => xs.foldRight(null: Throwable){ (m, th) => new Throwable(m.unMk, th) }

  def fullDisplay: Throwable => ExceptionDisplay =
    th => ExceptionDisplay(s"${ flatMessages(th) mkString separator }")

  def flatMessages: Throwable => Seq[ExceptionDisplay] =
    th => {

      @scala.annotation.tailrec
      def loop(c: Option[Throwable], acc: =>Vector[ExceptionDisplay]): Vector[ExceptionDisplay] =
        c match {
          case Some(inTh) => loop(Option(inTh.getCause), acc :+ ExceptionDisplay(inTh.getMessage))
          case None       => acc
        }

      loop(Option(th), Vector.empty)
    }
}