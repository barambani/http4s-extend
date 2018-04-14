package http4s.extend.test.laws.checks

import cats.Eq
import cats.laws.discipline._
import http4s.extend.Iso
import http4s.extend.laws.IsoLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll
import org.typelevel.discipline.Laws

private[test] sealed trait IsoLawsChecks[A, B] extends Laws {

  def laws: IsoLaws[A, B]

  def iso(
    implicit
      AA: Arbitrary[A],
      AB: Arbitrary[B],
      EA: Eq[A],
      EB: Eq[B]): RuleSet = {
    new RuleSet {

      def name: String = "iso"
      def bases: Seq[(String, RuleSet)] = Nil
      def parents: Seq[RuleSet] = Nil

      val props = Seq(
        "respects the left identity"  -> forAll(laws.leftIdentity _),
        "respects the right identity" -> forAll(laws.rightIdentity _)
      )
    }
  }
}

object IsoLawsChecks {

  @inline def apply[A, B : Iso[A, ?]]: IsoLawsChecks[A, B] =
    new IsoLawsChecks[A, B] {
      val laws: IsoLaws[A, B] = IsoLaws[A, B]
    }
}
