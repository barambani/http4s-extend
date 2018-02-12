package http4s.extend

private [extend] trait NewType { self =>
  type Base
  trait Tag extends Any
  type T <: Base with Tag
}