package http4s.extend.test

import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.test.laws.checks.IsoLawsChecks
import http4s.extend.{ExceptionDisplay, Void}

final class IsoDiscipline extends MinimalSuite {

  checkAll(
    "Iso[ExceptionDisplay, Throwable]",
    IsoLawsChecks[ExceptionDisplay, Throwable].iso
  )

  checkAll(
    "Iso[Throwable, ExceptionDisplay]",
    IsoLawsChecks[Throwable, ExceptionDisplay].iso
  )

  checkAll(
    "Iso[Void, Throwable]",
    IsoLawsChecks[Void, Throwable].iso
  )

  checkAll(
    "Iso[Throwable, Void]",
    IsoLawsChecks[Throwable, Void].iso
  )
}