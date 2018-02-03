package http4s.extend

package object instances {
  object sync               extends SyncInstances
  object errorInvariantMap  extends ErrorInvariantMapInstances
  object invariant          extends InvariantInstances
}