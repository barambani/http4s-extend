package http4s.extend

import cats.syntax.either._
import cats.{Applicative, Functor}

import scala.util.Either

trait Recoverable[E, F[_], G[_]] {

  def F: Functor[F]
  def G: Applicative[G]

  def recoverWith[A]: F[A] => (E => G[A]) => G[A]

  def recover[A]: F[A] => (E => A) => G[A] =
    fa => f => recoverWith(fa)(G.pure[A] _ compose f)

  def attempt[A]: F[A] => G[Either[E, A]] =
    fa => recover(F.map(fa)(_.asRight[E]))(_.asLeft)
}
