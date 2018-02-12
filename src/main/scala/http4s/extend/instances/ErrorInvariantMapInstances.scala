package http4s.extend.instances

import http4s.extend.util.ThrowableModule._
import http4s.extend.{ErrorInvariantMap, ExceptionDisplay}

trait ErrorInvariantMapInstances {

  implicit def throwableStringErrMap: ErrorInvariantMap[Throwable, ExceptionDisplay] =
    new ErrorInvariantMap[Throwable, ExceptionDisplay] {
      def direct: Throwable => ExceptionDisplay =
        fullDisplay

      def reverse: ExceptionDisplay => Throwable =
        throwableOf
    }
}