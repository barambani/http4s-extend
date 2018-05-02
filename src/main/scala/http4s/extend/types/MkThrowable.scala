package http4s.extend.types

import http4s.extend.NewType

abstract class MkThrowable extends NewType {
  def apply(b: Throwable): T = b.asInstanceOf[T]
  def mkF[F[_]](fs: F[Throwable]): F[T] = fs.asInstanceOf[F[T]]
}

private[types] object MkThrowable {
  implicit final class MkThrowableSyntax(val `this`: MkThrowable#T) extends AnyVal {
    def unMk: Throwable = `this`.asInstanceOf[Throwable]
  }
}