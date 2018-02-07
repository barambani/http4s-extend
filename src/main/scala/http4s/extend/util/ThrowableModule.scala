package http4s.extend.util

import http4s.extend.Algebra.ExceptionMessage

import scala.annotation.tailrec

object ThrowableModule {

  private val separator = "\n\rcaused by "

  def throwableOfMessage: ExceptionMessage => Throwable =
    xs => foldedThrowable(xs.message.split(separator).toSeq map (new ExceptionMessage(_)))

  def foldedThrowable: Seq[ExceptionMessage] => Throwable =
    xs => xs.foldRight(null: Throwable){ (m, th) => new Throwable(m.message, th) }

  def completeMessage: Throwable => ExceptionMessage =
    th => new ExceptionMessage(
      s"${ flatMessage(th) map (_.message) mkString separator }"
    )

  def flatMessage: Throwable => Seq[ExceptionMessage] =
    th => {

      @tailrec
      def loop(c: Option[Throwable], acc: =>Vector[ExceptionMessage]): Vector[ExceptionMessage] =
        c match {
          case Some(inTh) => loop(Option(inTh.getCause), acc :+ new ExceptionMessage(inTh.getMessage))
          case None       => acc
        }

      loop(Option(th), Vector.empty)
    }

}
