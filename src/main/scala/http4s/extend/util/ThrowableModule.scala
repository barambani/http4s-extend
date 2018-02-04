package http4s.extend.util

import http4s.extend.Algebra.ThrowableCompleteMessage

import scala.annotation.tailrec

object ThrowableModule {

  private val separator = "\n\rcaused by "

  def throwableOfMessage: ThrowableCompleteMessage => Throwable =
    xs => foldedThrowable(xs.message.split(separator).toSeq map (new ThrowableCompleteMessage(_)))

  def foldedThrowable: Seq[ThrowableCompleteMessage] => Throwable =
    xs => xs.foldRight(null: Throwable){ (m, th) => new Throwable(m.message, th) }

  def completeMessage: Throwable => ThrowableCompleteMessage =
    th => new ThrowableCompleteMessage(
      s"${ flatMessages(th) map (_.message) mkString separator }"
    )

  def flatMessages: Throwable => Seq[ThrowableCompleteMessage] =
    th => {

      @tailrec
      def loop(c: Option[Throwable], acc: =>Vector[ThrowableCompleteMessage]): Vector[ThrowableCompleteMessage] =
        c match {
          case Some(inTh) => loop(Option(inTh.getCause), acc :+ new ThrowableCompleteMessage(inTh.getMessage))
          case None       => acc
        }

      loop(Option(th), Vector.empty)
    }

}
