package http4s.extend.laws

import cats.laws._
import http4s.extend.Recoverable

trait RecoverableLaws[E, F[_], G[_]] {

  implicit def re: Recoverable[E, F, G]

  def flatMapNeverProgressesOnFailOfF[A](e: E, f: A => F[A]) =
    re.M.flatMap(re.raiseError(e))(f) <-> re.raiseError[A](e)

  def flatMapNeverStopsForG[A](ga: G[A], f: E => A) =
    (re.recover[A] compose re.toFallible[A])(ga)(f) <-> ga

  def absolveAttemptNotChangingFa[A](fa: F[A]) =
    (re.absolve[A] compose re.attempt[A])(fa) <-> fa
}

object RecoverableLaws {

  @inline def apply[E, F[_], G[_]](implicit ev: Recoverable[E, F, G]): RecoverableLaws[E, F, G] =
    new RecoverableLaws[E, F, G] {
      def re: Recoverable[E, F, G] = ev
    }
}