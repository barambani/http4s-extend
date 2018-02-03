package http4s.extend

import cats.data.ValidatedNel

package object syntax {

  type Verified[A] = ValidatedNel[String, A]

  object all                  extends AllSyntax
  object byNameNt             extends ByNameNaturalTransformationSyntax
  object errorAdapt           extends ErrorAdaptSyntax
  object monadError           extends MonadErrorModuleSyntax
  object httpService          extends Http4sServiceSyntax
  object circe                extends CirceSyntax
  object responseVerification extends ResponseVerificationSyntax
  object eq                   extends EqSyntax
}