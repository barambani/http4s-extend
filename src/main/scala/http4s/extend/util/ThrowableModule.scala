package http4s.extend.util

import http4s.extend.ExceptionDisplayType._

import scala.annotation.tailrec

object ThrowableModule {

  import ExceptionDisplay._

  private val separator = "\n\rcaused by "

  def throwableOf: ExceptionDisplay => Throwable =
    xs => throwableHierarchy {
      unwrap(xs).split(separator).toSeq map ExceptionDisplay.apply
    }

  def throwableHierarchy: Seq[ExceptionDisplay] => Throwable =
    xs => xs.foldRight(null: Throwable){ (m, th) => new Throwable(unwrap(m), th) }

  def fullDisplay: Throwable => ExceptionDisplay =
    th => ExceptionDisplay(s"${ flatMessages(th) mkString separator }")

  def flatMessages: Throwable => Seq[ExceptionDisplay] =
    th => {

      @tailrec
      def loop(c: Option[Throwable], acc: =>Vector[ExceptionDisplay]): Vector[ExceptionDisplay] =
        c match {
          case Some(inTh) => loop(Option(inTh.getCause), acc :+ ExceptionDisplay(inTh.getMessage))
          case None       => acc
        }

      loop(Option(th), Vector.empty)
    }

}
