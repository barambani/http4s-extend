package http4s.extend

trait Recoverable[E, F[_], G[_]] {

  def handleErrorWith[A](fa: F[A])(f: E => G[A]): G[A]

  def handleError[A](fa: F[A])(f: E => A): G[A]
}
