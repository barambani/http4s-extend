package http4s.extend.util

import http4s.extend.ExceptionDisplay

import scala.annotation.tailrec

object ThrowableModule {

  import ExceptionDisplay._

  private val separator = "\n\rcaused by "

  def throwableOf: ExceptionDisplay => Throwable =
    xs => throwableHierarchy {
      unMk(xs).split(separator).toSeq map ExceptionDisplay.mk
    }

  def throwableHierarchy: Seq[ExceptionDisplay] => Throwable =
    xs => xs.foldRight(null: Throwable){ (m, th) => new Throwable(unMk(m), th) }

  def fullDisplay: Throwable => ExceptionDisplay =
    th => ExceptionDisplay.mk(s"${ flatMessages(th) mkString separator }")

  def flatMessages: Throwable => Seq[ExceptionDisplay] =
    th => {

      @tailrec
      def loop(c: Option[Throwable], acc: =>Vector[ExceptionDisplay]): Vector[ExceptionDisplay] =
        c match {
          case Some(inTh) => loop(Option(inTh.getCause), acc :+ ExceptionDisplay.mk(inTh.getMessage))
          case None       => acc
        }

      loop(Option(th), Vector.empty)
    }
}
