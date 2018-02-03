package http4s.extend.test

import http4s.extend.ErrorInvariantMap
import http4s.extend.Model.ThrowableCompleteMessage
import http4s.extend.test.Fixtures.TestError
import http4s.extend.util.ThrowableModule.{completeMessage, wrapCompleteMessage}

trait Fixtures {

  def testErrorMap: ErrorInvariantMap[Throwable, TestError] =
    new ErrorInvariantMap[Throwable, TestError] {
      def direct: Throwable => TestError =
        th => TestError(completeMessage(th))

      def reverse: TestError => Throwable =
        er => wrapCompleteMessage(er.error)
    }
}

object Fixtures {
  case class TestError(error: ThrowableCompleteMessage)
}
