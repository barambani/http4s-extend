package http4s.extend.test.laws.checks

import cats.Eq
import cats.effect.IO
import cats.laws.discipline._
import http4s.extend.Effectful
import http4s.extend.laws.EffectfulLaws
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Cogen, Prop}
import org.typelevel.discipline.Laws

sealed trait EffectfulLawsChecks[F[_]] extends Laws {

  def laws: EffectfulLaws[F]

  def effectful[A: Arbitrary: Eq](
    implicit
      ArbFA: Arbitrary[F[A]],
      ArbT: Arbitrary[Throwable],
      CogenA: Cogen[A],
      EqFA: Eq[F[A]],
      EqFU: Eq[F[Unit]],
      EqFEitherTA: Eq[F[Either[Throwable, A]]],
      EqFInt: Eq[F[Int]],
      EqIOA: Eq[IO[A]],
      EqIOU: Eq[IO[Unit]],
      EqIOEitherTA: Eq[IO[Either[Throwable, A]]]): RuleSet = {
    new RuleSet {

      def name: String = "effectful"
      def bases: Seq[(String, RuleSet)] = Nil
      def parents: Seq[RuleSet] = Nil

      val props = Seq(
        "runAsync pure produces right IO"         -> forAll(laws.runAsyncPureProducesRightIO[A] _),
        "runAsync raiseError produces left IO"    -> forAll(laws.runAsyncRaiseErrorProducesLeftIO[A] _),
        "runAsync ignores error in handler"       -> forAll(laws.runAsyncIgnoresErrorInHandler[A] _),
        "repeated callback ignored"               -> forAll(laws.repeatedCallbackIgnored[A] _),
        "async right is pure"                     -> forAll(laws.asyncRightIsPure[A] _),
        "async left is raiseError"                -> forAll(laws.asyncLeftIsRaiseError[A] _),
        "repeated async evaluation not memoized"  -> forAll(laws.repeatedAsyncEvaluationNotMemoized[A] _),
        "propagate errors through bind (async)"   -> forAll(laws.propagateErrorsThroughBindAsync[A] _),
        "delay constant is pure"                  -> forAll(laws.delayConstantIsPure[A] _),
        "suspend constant is pure join"           -> forAll(laws.suspendConstantIsPureJoin[A] _),
        "throw in delay is raiseError"            -> forAll(laws.delayThrowIsRaiseError[A] _),
        "throw in suspend is raiseError"          -> forAll(laws.suspendThrowIsRaiseError[A] _),
        "unsequenced delay is no-op"              -> forAll(laws.unsequencedDelayIsNoop[A] _),
        "repeated sync evaluation not memoized"   -> forAll(laws.repeatedSyncEvaluationNotMemoized[A] _),
        "propagate errors through bind (suspend)" -> forAll(laws.propagateErrorsThroughBindSuspend[A] _),
        "bind suspends evaluation"                -> forAll(laws.bindSuspendsEvaluation[A] _),
        "map suspends evaluation"                 -> forAll(laws.mapSuspendsEvaluation[A] _),
        "stack-safe on left-associated binds"     -> Prop.lzy(laws.stackSafetyOnRepeatedLeftBinds),
        "stack-safe on right-associated binds"    -> Prop.lzy(laws.stackSafetyOnRepeatedRightBinds),
        "stack-safe on repeated attempts"         -> Prop.lzy(laws.stackSafetyOnRepeatedAttempts),
        "stack-safe on repeated maps"             -> Prop.lzy(laws.stackSafetyOnRepeatedMaps)
      )
    }
  }
}

object EffectfulLawsChecks {

  @inline def apply[F[_]](implicit ev: Effectful[F]): EffectfulLawsChecks[F] =
    new EffectfulLawsChecks[F] {
      def laws: EffectfulLaws[F] = EffectfulLaws[F]
    }
}