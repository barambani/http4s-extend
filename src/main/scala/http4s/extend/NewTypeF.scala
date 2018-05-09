package http4s.extend

trait NewTypeF { self =>
  private [extend] type Base <: Any
  private [extend] trait Tag extends Any
  type T[_] <: Base with Tag
}
