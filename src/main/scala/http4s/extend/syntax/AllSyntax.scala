package http4s.extend.syntax

trait AllSyntax
  extends Http4sServiceSyntax
  with    ByNameNaturalTransformationSyntax
  with    ErrorAdaptSyntax
  with    MonadErrorModuleSyntax
  with    CirceSyntax
  with    ResponseVerificationSyntax
  with    EqSyntax
  with    InvariantSyntax