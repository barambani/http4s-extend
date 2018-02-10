package http4s.extend

import cats.data.ValidatedNel

package object syntax {

  type Verified[A] = ValidatedNel[String, A]

  object eq                   extends EqSyntax
  object all                  extends AllSyntax
  object circe                extends CirceSyntax
  object byNameNt             extends ByNameNtSyntax
  object invariant            extends InvariantSyntax
  object errorAdapt           extends ErrorAdaptSyntax
  object httpService          extends Http4sServiceSyntax
  object monadError           extends MonadErrorModuleSyntax
  object responseVerification extends ResponseVerificationSyntax
}