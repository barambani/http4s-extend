package http4s.extend

package object syntax {
  object all          extends AllSyntax
  object byNameNt     extends ByNameNaturalTransformationSyntax
  object errorAdapt   extends ErrorAdaptSyntax
  object monadError   extends MonadErrorModuleSyntax
  object httpService  extends Http4sServiceSyntax
  object circe        extends CirceSyntax
}