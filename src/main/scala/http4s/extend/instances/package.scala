package http4s.extend

package object instances {
  object eq                 extends EqInstances
  object sync               extends SyncInstances
  object invariant          extends InvariantInstances
  object errorInvariantMap  extends ErrorInvariantMapInstances
}