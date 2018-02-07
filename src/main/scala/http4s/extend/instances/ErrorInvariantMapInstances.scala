package http4s.extend.instances

import http4s.extend.Algebra.ExceptionMessage
import http4s.extend.ErrorInvariantMap
import http4s.extend.util.ThrowableModule._

trait ErrorInvariantMapInstances {

  implicit def throwableStringErrMap: ErrorInvariantMap[Throwable, ExceptionMessage] =
    new ErrorInvariantMap[Throwable, ExceptionMessage] {
      def direct: Throwable => ExceptionMessage =
        completeMessage

      def reverse: ExceptionMessage => Throwable =
        throwableOfMessage
    }
}