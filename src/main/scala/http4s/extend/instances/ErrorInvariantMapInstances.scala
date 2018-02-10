package http4s.extend.instances

import http4s.extend.ErrorInvariantMap
import http4s.extend.ExceptionDisplayType._
import http4s.extend.util.ThrowableModule._

trait ErrorInvariantMapInstances {

  implicit def throwableStringErrMap: ErrorInvariantMap[Throwable, ExceptionDisplay] =
    new ErrorInvariantMap[Throwable, ExceptionDisplay] {
      def direct: Throwable => ExceptionDisplay =
        fullDisplay

      def reverse: ExceptionDisplay => Throwable =
        throwableOf
    }
}