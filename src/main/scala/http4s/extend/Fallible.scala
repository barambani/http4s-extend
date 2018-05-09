package http4s.extend

import cats.Monad

import scala.util.Either

trait Fallible[E, F[_], G[_]] {

  def M: Monad[F]

  def raiseError[A]: E => F[A]
  def toFallible[A]: G[A] => F[A]

  def absolve[A]: G[Either[E, A]] => F[A] =
    ga => M.flatMap(toFallible(ga)) { _.fold(raiseError, M.pure) }
}