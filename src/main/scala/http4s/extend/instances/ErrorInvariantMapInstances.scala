package http4s.extend.instances

import http4s.extend.ErrorInvariantMap

trait ErrorInvariantMapInstances {

  implicit def throwableStringErrMap: ErrorInvariantMap[Throwable, String] =
    new ErrorInvariantMap[Throwable, String] {
      def direct: Throwable => String =
        _.getMessage

      def reverse: String => Throwable =
        new Throwable(_)
    }
}
