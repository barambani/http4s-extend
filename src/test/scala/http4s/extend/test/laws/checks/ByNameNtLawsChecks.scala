package http4s.extend.test.laws.checks

import cats.laws.discipline._
import cats.{Eq, Functor}
import http4s.extend.ByNameNt.~~>
import http4s.extend.laws.ByNameNtLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll
import org.typelevel.discipline.Laws

private[test] sealed trait ByNameNtLawsChecks[F[_], G[_]] extends Laws {

  def laws: ByNameNtLaws[F, G]

  def transformation[A : Arbitrary : Eq, B : Arbitrary : Eq](
    implicit
      ArbFA: Arbitrary[F[A]],
      ArbAB: Arbitrary[A => B],
      EqGB: Eq[G[B]]): RuleSet = {
    new RuleSet {

      def name: String = "transformation"
      def bases: Seq[(String, RuleSet)] = Nil
      def parents: Seq[RuleSet] = Nil

      val props = Seq(
        "respects the naturality condition" -> forAll(laws.naturalityCondition[A, B] _)
      )
    }
  }
}

object ByNameNtLawsChecks {

  @inline def apply[F[_] : Functor : ?[_] ~~> G , G[_] : Functor]: ByNameNtLawsChecks[F, G] =
    new ByNameNtLawsChecks[F, G] {
      val laws: ByNameNtLaws[F, G] = ByNameNtLaws[F, G]
    }
}
