package http4s.extend.util

import http4s.extend.ExceptionDisplayType._

import scala.annotation.tailrec

object ThrowableModule {

  import ExceptionDisplay._

  private val separator = "\n\rcaused by "

  def throwableOfMessage: ExceptionDisplay => Throwable =
    xs => foldedThrowable(unwrap(xs).split(separator).toSeq map ExceptionDisplay.apply)

  def foldedThrowable: Seq[ExceptionDisplay] => Throwable =
    xs => xs.foldRight(null: Throwable){ (m, th) => new Throwable(unwrap(m), th) }

  def completeMessage: Throwable => ExceptionDisplay =
    th => ExceptionDisplay(s"${ flatMessage(th) mkString separator }")

  def flatMessage: Throwable => Seq[ExceptionDisplay] =
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
