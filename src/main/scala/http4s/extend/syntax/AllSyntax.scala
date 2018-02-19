package http4s.extend.syntax

trait AllSyntax
  extends Http4sServiceSyntax
  with    ByNameNtSyntax
  with    ErrorAdaptSyntax
  with    CirceSyntax
  with    ResponseVerificationSyntax
  with    EqSyntax