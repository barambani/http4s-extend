package http4s.extend.instances

import cats.Invariant
import http4s.extend.ErrorInvariantMap

trait InvariantInstances {

  implicit def errorMapInvariant[E]: Invariant[ErrorInvariantMap[E, ?]] =
    new Invariant[ErrorInvariantMap[E, ?]] {

      def imap[A, B](fa: ErrorInvariantMap[E, A])(f: A => B)(g: B => A): ErrorInvariantMap[E, B] =
        new ErrorInvariantMap[E, B] {
          def direct: E => B =
            f compose fa.direct

          def reverse: B => E =
            fa.reverse compose g
        }
    }
}
