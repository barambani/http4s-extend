package http4s.extend

trait NewType { self =>
  private [extend] type Base
  private [extend] trait Tag extends Any
  type T <: Base with Tag
}