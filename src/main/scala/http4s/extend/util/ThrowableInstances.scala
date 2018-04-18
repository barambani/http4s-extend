package http4s.extend.util

import cats.MonadError
import scalaz.concurrent.{Task => ScalazTask}

/**
  * Provides an orphan instance for a `MonadError[ScalazTask, Throwable]`. The only alternative
  * I know about to this is to use shims, but for the moment I prefer not to add a full dependency
  * only for this. This might change in the future
  */
private[extend] trait ThrowableInstances {
  implicit val scalazThrowableMonadError: MonadError[ScalazTask, Throwable] =
    MonadErrorUtil.scalazMonadError[ScalazTask, Throwable, Throwable]
}
