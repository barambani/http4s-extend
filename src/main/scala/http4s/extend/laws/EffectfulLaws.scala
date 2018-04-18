package http4s.extend.laws

import cats.MonadError
import cats.laws._
import cats.syntax.apply._
import cats.syntax.either._
import http4s.extend.Effectful

/**
  * Effectful, together with its MonadError evidence, should abide by the same laws as cats.effect.Effect
  * 
  * The code below is adapted from the Cats Effect Laws:
  * https://github.com/typelevel/cats-effect/tree/master/laws/shared/src/main/scala/cats/effect/laws
  */
sealed trait EffectfulLaws[E, F[_]] {

  implicit def eff: Effectful[E, F]
  implicit def monadError: MonadError[F, E]

  /**
    * Laws from cats.effect Effect[F]
    */
  def runAsyncPureProducesRightIO[A](a: A) = {
    val fa = monadError.pure(a)
    var result: Option[Either[E, A]] = None
    val read = eff delay { result.get }

    eff.runAsync(fa)(e => eff.delay { result = Some(e) }) *> read <-> eff.point(a.asRight)
  }

  def runAsyncRaiseErrorProducesLeftIO[A](e: E) = {
    val fa: F[A] = monadError.raiseError(e)
    var result: Option[Either[E, A]] = None
    val read = eff delay { result.get }

    eff.runAsync(fa)(e => eff.delay { result = Some(e) }) *> read <-> eff.point(e.asLeft)
  }

  def runAsyncIgnoresErrorInHandler[A](e: E) = {
    val fa = monadError.pure(())
    eff.runAsync(fa)(_ => monadError.raiseError(e)) <-> eff.point(())
  }

  def repeatedCallbackIgnored[A](a: A, f: A => A) = {
    var cur = a
    val change = eff delay { cur = f(cur) }
    val readResult = eff delay cur

    val double: F[Unit] = eff async { cb =>
      cb(Right(()))
      cb(Right(()))
    }

    val test = eff.runAsync(double *> change) { _ => eff.unit }

    test *> readResult <-> eff.point(f(a))
  }

  /**
    * Laws from cats.effect Async[F]
    */
  def asyncRightIsPure[A](a: A) =
    eff.async[A](_(Right(a))) <-> monadError.pure(a)

  def asyncLeftIsRaiseError[A](e: E) =
    eff.async[A](_(Left(e))) <-> monadError.raiseError(e)

  def repeatedAsyncEvaluationNotMemoized[A](a: A, f: A => A) = {
    var cur = a

    val change: F[Unit] = eff async { cb =>
      cur = f(cur)
      cb(Right(()))
    }

    val read: F[A] = eff.delay(cur)

    change *> change *> read <-> monadError.pure(f(f(a)))
  }

  def propagateErrorsThroughBindAsync[A](t: E) = {
    val fa = monadError.attempt(monadError.flatMap(eff.async[A](_ (Left(t))))(x => monadError.pure(x)))

    fa <-> monadError.pure(Left(t))
  }

  /**
    * Laws from cats.effect Sync[F]
    */
  def delayConstantIsPure[A](a: A) =
    eff.delay(a) <-> monadError.pure(a)

  def suspendConstantIsPureJoin[A](fa: F[A]) =
    eff.suspend(fa) <-> monadError.flatten(monadError.pure(fa))

  def failIsRaiseError[A](e: E) =
    eff.fail(e) <-> monadError.raiseError(e)

  def unsequencedDelayIsNoop[A](a: A, f: A => A) = {
    var cur = a
    val change = eff delay { cur = f(cur) }
    val _ = change

    eff.delay(cur) <-> monadError.pure(a)
  }

  def repeatedSyncEvaluationNotMemoized[A](a: A, f: A => A) = {
    var cur = a
    val change = eff delay { cur = f(cur) }
    val read = eff.delay(cur)

    change *> change *> read <-> monadError.pure(f(f(a)))
  }

  def bindSuspendsEvaluation[A](fa: F[A], a1: A, f: (A, A) => A) = {
    var state = a1
    val evolve = monadError.flatMap(fa) { a2 =>
      state = f(state, a2)
      monadError.pure(state)
    }
    // Observing `state` before and after `evolve`
    monadError.map2(monadError.pure(state), evolve)(f) <-> monadError.map(fa)(a2 => f(a1, f(a1, a2)))
  }

  def mapSuspendsEvaluation[A](fa: F[A], a1: A, f: (A, A) => A) = {
    var state = a1
    val evolve = monadError.map(fa) { a2 =>
      state = f(state, a2)
      state
    }
    // Observing `state` before and after `evolve`
    monadError.map2(monadError.pure(state), evolve)(f) <-> monadError.map(fa)(a2 => f(a1, f(a1, a2)))
  }

  def stackSafetyOnRepeatedLeftBinds = {
    val result = (0 until 10000).foldLeft(eff.delay(())) { (acc, _) =>
      monadError.flatMap(acc)(_ => eff.delay(()))
    }

    result <-> monadError.pure(())
  }

  def stackSafetyOnRepeatedRightBinds = {
    val result = (0 until 10000).foldRight(eff.delay(())) { (_, acc) =>
      monadError.flatMap(eff.delay(()))(_ => acc)
    }

    result <-> monadError.pure(())
  }

  def stackSafetyOnRepeatedAttempts = {
    // Note this isn't enough to guarantee stack safety, unless
    // coupled with `bindSuspendsEvaluation`
    val result = (0 until 10000).foldLeft(eff.delay(())) { (acc, _) =>
      monadError.map(monadError.attempt(acc))(_ => ())
    }
    result <-> monadError.pure(())
  }

  def stackSafetyOnRepeatedMaps = {
    // Note this isn't enough to guarantee stack safety, unless
    // coupled with `mapSuspendsEvaluation`
    val result = (0 until 10000).foldLeft(eff.delay(0)) { (acc, _) =>
      monadError.map(acc)(_ + 1)
    }
    result <-> monadError.pure(10000)
  }
}

object EffectfulLaws {

  @inline def apply[E, F[_]](implicit ev1: Effectful[E, F], ev2: MonadError[F, E]): EffectfulLaws[E, F] =
    new EffectfulLaws[E, F] {
      def eff: Effectful[E, F] = ev1
      def monadError: MonadError[F, E] = ev2
    }
}
