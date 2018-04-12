package http4s.extend.types

import http4s.extend.NewType

object MkVoid extends NewType {
  def mk(b: Unit): T = b.asInstanceOf[T]
  def unMk(t: T): Unit = t.asInstanceOf[Unit]
  def mkF[F[_]](fs: F[Unit]): F[T] = fs.asInstanceOf[F[T]]
}