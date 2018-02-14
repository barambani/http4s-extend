package http4s.extend

/**
  * Any error invariant map instance must abide by the following laws
  *
  *   (reverse compose direct)(e1) <-> e1
  *   (direct compose reverse)(e2) <-> e2
  *
  *   where e1 :: E1 and e2 :: E2
  *
  *  Instances can be checked using cats.laws.discipline.PIsoTests.
  *  See this [[https://github.com/barambani/http4s-poc-api/blob/master/src/test/scala/laws/checks/ErrorInvariantMapLawsChecks.scala#L11 example]]
  */
trait ErrorInvariantMap[E1, E2] {
  def direct: E1 => E2
  def reverse: E2 => E1
}

object ErrorInvariantMap {
  @inline def apply[E1, E2](implicit E: ErrorInvariantMap[E1, E2]): ErrorInvariantMap[E1, E2] = E
}