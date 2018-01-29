package http4s.extend.syntax

import java.nio.charset.StandardCharsets

import cats.data.Validated
import cats.instances.string._
import cats.syntax.apply._
import cats.syntax.eq._
import cats.syntax.show._
import cats.syntax.validated._
import cats.{Eq, Show}
import http4s.extend.ErrorInvariantMap
import http4s.extend.instances.sync._
import http4s.extend.syntax.responseVerification._
import org.http4s.{EntityDecoder, Response, Status}

trait ResponseVerificationSyntax {

  implicit def verifiedSyntax[A : Eq : Show](a: A) = new VerifiedOps(a)
  implicit def verifiedOptionSyntax[A : Eq : Show](a: Option[A]) = new VerifiedOptionOps(a)

  implicit def responseVerificationSyntax[E : ErrorInvariantMap[Throwable, ?]](result: Either[E, Response[Either[E, ?]]]) =
    new EitherResultOps(result)
}

final class EitherResultOps[E : ErrorInvariantMap[Throwable, ?]](result: Either[E, Response[Either[E, ?]]]) {

  import org.http4s.Http4s._

  def verify[A : EntityDecoder[Either[E, ?], ?]](status: Status, check: A => Verified[A]): Verified[A] =
    result.fold(
      err => s"Should succeed but returned the error $err".invalidNel,
      res => (res.status isSameAs status, verifiedResponse[A](res, check)) mapN { (_, a) => a }
    )

  def verifyResponseText(status: Status, expected: String): Verified[String] =
    result.fold(
      err => s"Should succeed but returned the error $err".invalidNel,
      res => (res.status isSameAs status, verifiedResponseText(res, expected)) mapN { (_, a) => a }
    )

  private def verifiedResponse[A : EntityDecoder[Either[E, ?], ?]](res: Response[Either[E, ?]], check: A => Verified[A]): Verified[A] =
    res.as[A].fold(
      respErr => s"Response should succeed but returned the error $respErr".invalidNel,
      respRes => check(respRes)
    )

  private def verifiedResponseText[A](res: Response[Either[E, ?]], expected: String): Verified[String] =
    res.body.compile.toVector.map(_.toArray).fold(
      respErr => s"Response should succeed but returned the error $respErr".invalidNel,
      respMsg => new String(respMsg, StandardCharsets.UTF_8) isSameAs expected
    )
}

final class VerifiedOps[A : Eq : Show](a: A) {

  def isNotSameAs(expected: =>A): Verified[A] =
    Validated.condNel(a =!= expected, a, s"Unexpected value. Expected different from ${expected.show} but was ${a.show}")

  def isSameAs(expected: =>A): Verified[A] =
    Validated.condNel(a === expected, a, s"Unexpected value. Expected ${expected.show} but was ${a.show}")

  def is(p: A => Boolean, reason: =>String = ""): Verified[A] =
    Validated.condNel(p(a), a, s"Unexpected value ${a.show}: Reason $reason")
}

final class VerifiedOptionOps[A : Eq : Show](a: Option[A]) {

  def isNotEmpty: Verified[Option[A]] =
    Validated.condNel(a.isDefined, a, s"Unexpected empty option value")
}
