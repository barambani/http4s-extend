package http4s.extend

import cats.data.ValidatedNel

package object syntax {

  type Verified[A] = ValidatedNel[String, A]

  object eq                   extends EqSyntax
  object all                  extends AllSyntax
  object circe                extends CirceSyntax
  object byNameNt             extends ByNameNtSyntax
  object errorAdapt           extends ErrorAdaptSyntax
  object httpService          extends Http4sServiceSyntax
  object responseVerification extends ResponseVerificationSyntax
  object parEffectful         extends ParEffectfulSyntax
  object errorResponse        extends ErrorResponseSyntax
}