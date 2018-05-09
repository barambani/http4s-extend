package http4s.extend.laws

import cats.Monad
import cats.laws._
import cats.syntax.apply._
import cats.syntax.either._
import http4s.extend.Effectful

/**
  * Effectful of `F[_]`, when there is evidence of a Monad for `F[_]`, should abide by the same laws as cats.effect.Effect
  * 
  * The code below is adapted from the Cats Effect Laws to prove that the abstractions give the same guarantees:
  * https://github.com/typelevel/cats-effect/tree/master/laws/shared/src/main/scala/cats/effect/laws
  */
sealed trait EffectfulLaws[E, F[_]] {

  implicit val evidence: Monad[F] = eff.M
  implicit def eff: Effectful[E, F]

  /**
    * Laws for Effectful and its Monad
    */
  def flatMapNotProgressOnFail[A](e: E, f: A => F[A]) =
    eff.M.flatMap(eff.fail(e))(f) <-> eff.fail[A](e)

  /**
    * Laws from cats.effect Effect[F]
    */
  def runAsyncPureProducesRightIO[A](a: A) = {
    val fa = eff.point(a)
    var result: Option[Either[E, A]] = None
    val read = eff delay { result.get }

    eff.runAsync(fa)(e => eff.delay { result = Some(e) }) *> read <-> eff.point(a.asRight)
  }

  def runAsyncRaiseErrorProducesLeftIO[A](e: E) = {
    val fa: F[A] = eff.fail[A](e)
    var result: Option[Either[E, A]] = None
    val read = eff delay { result.get }

    eff.runAsync(fa)(e => eff.delay { result = Some(e) }) *> read <-> eff.point(e.asLeft)
  }

  def runAsyncIgnoresErrorInHandler[A](e: E) = {
    val fa = eff.point(())
    eff.runAsync(fa)(_ => eff.fail[Unit](e)) <-> eff.point(())
  }

  def repeatedCallbackIgnored[A](a: A, f: A => A) = {
    var cur = a
    val change = eff.delay { cur = f(cur) }
    val readResult = eff.delay { cur }

    val double: F[Unit] = eff.async { cb =>
      cb(Right(()))
      cb(Right(()))
    }

    double *> change *> readResult <-> eff.delay(f(a))
  }

  /**
    * Laws from cats.effect Async[F]
    */
  def asyncRightIsPure[A](a: A) =
    eff.async[A](_(Right(a))) <-> eff.point(a)

  def asyncLeftIsRaiseError[A](e: E) =
    eff.async[A](_(Left(e))) <-> eff.fail[A](e)

  def repeatedAsyncEvaluationNotMemoized[A](a: A, f: A => A) = {
    var cur = a

    val change: F[Unit] = eff async { cb =>
      cur = f(cur)
      cb(Right(()))
    }

    val read: F[A] = eff.delay(cur)

    change *> change *> read <-> eff.point(f(f(a)))
  }

  def propagateErrorsThroughBindAsync[A](t: E) = {
    val fa = eff.M.flatMap(eff.async[A](_ (Left(t))))(x => eff.point(x))

    fa <-> eff.fail[A](t)
  }

  /**
    * Laws from cats.effect Sync[F]
    */
  def delayConstantIsPure[A](a: A) =
    eff.delay(a) <-> eff.point(a)

  def suspendConstantIsPureJoin[A](fa: F[A]) =
    eff.suspend(fa) <-> eff.M.flatten(eff.point(fa))

  def unsequencedDelayIsNoop[A](a: A, f: A => A) = {
    var cur = a
    val change = eff delay { cur = f(cur) }
    val _ = change

    eff.delay(cur) <-> eff.point(a)
  }

  def repeatedSyncEvaluationNotMemoized[A](a: A, f: A => A) = {
    var cur = a
    val change = eff delay { cur = f(cur) }
    val read = eff.delay(cur)

    change *> change *> read <-> eff.point(f(f(a)))
  }

  def bindSuspendsEvaluation[A](fa: F[A], a1: A, f: (A, A) => A) = {
    var state = a1
    val evolve = eff.M.flatMap(fa) { a2 =>
      state = f(state, a2)
      eff.point(state)
    }
    // Observing `state` before and after `evolve`
    eff.M.map2(eff.point(state), evolve)(f) <-> eff.M.map(fa)(a2 => f(a1, f(a1, a2)))
  }

  def mapSuspendsEvaluation[A](fa: F[A], a1: A, f: (A, A) => A) = {
    var state = a1
    val evolve = eff.M.map(fa) { a2 =>
      state = f(state, a2)
      state
    }
    // Observing `state` before and after `evolve`
    eff.M.map2(eff.point(state), evolve)(f) <-> eff.M.map(fa)(a2 => f(a1, f(a1, a2)))
  }

  def stackSafetyOnRepeatedLeftBinds = {
    val result = (0 until 10000).foldLeft(eff.delay(())) { (acc, _) =>
      eff.M.flatMap(acc)(_ => eff.delay(()))
    }

    result <-> eff.point(())
  }

  def stackSafetyOnRepeatedRightBinds = {
    val result = (0 until 10000).foldRight(eff.delay(())) { (_, acc) =>
      eff.M.flatMap(eff.delay(()))(_ => acc)
    }

    result <-> eff.point(())
  }

  def stackSafetyOnRepeatedMaps = {
    // Note this isn't enough to guarantee stack safety, unless
    // coupled with `mapSuspendsEvaluation`
    val result = (0 until 10000).foldLeft(eff.delay(0)) { (acc, _) =>
      eff.M.map(acc)(_ + 1)
    }
    result <-> eff.point(10000)
  }
}

object EffectfulLaws {

  @inline def apply[E, F[_]](implicit ev: Effectful[E, F]): EffectfulLaws[E, F] =
    new EffectfulLaws[E, F] {
      def eff: Effectful[E, F] = ev
    }
}
