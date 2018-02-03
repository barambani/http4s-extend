package http4s.extend.instances

import http4s.extend.ErrorInvariantMap
import http4s.extend.Model.ThrowableCompleteMessage
import http4s.extend.util.ThrowableModule._

trait ErrorInvariantMapInstances {

  implicit def throwableStringErrMap: ErrorInvariantMap[Throwable, ThrowableCompleteMessage] =
    new ErrorInvariantMap[Throwable, ThrowableCompleteMessage] {
      def direct: Throwable => ThrowableCompleteMessage =
        completeMessage

      def reverse: ThrowableCompleteMessage => Throwable =
        wrapCompleteMessage
    }
}