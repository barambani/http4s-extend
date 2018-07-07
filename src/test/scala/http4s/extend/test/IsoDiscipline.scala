package http4s.extend.test

import http4s.extend.ExceptionDisplay
import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.test.laws.checks.IsoLawsChecks

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
    "Iso[Throwable, Throwable]",
    IsoLawsChecks[Throwable, Throwable].iso
  )
}