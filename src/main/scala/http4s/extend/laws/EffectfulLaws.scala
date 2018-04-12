package http4s.extend.laws

import cats.MonadError
import cats.effect.IO
import cats.laws._
import cats.syntax.apply._
import http4s.extend.Effectful
import cats.syntax.either._

/**
  * Effectful, together with its MonadError evidence, should abide by the same laws as cats.effect.Effect
  * 
  * The code below is adapted from the Cats Effect Laws:
  * https://github.com/typelevel/cats-effect/tree/master/laws/shared/src/main/scala/cats/effect/laws
  */
sealed trait EffectfulLaws[E, F[_]] {

  implicit def ev: Effectful[E, F]
  implicit def monadError: MonadError[F, E]

  /**
    * Laws from cats.effect Effect[F]
    */
  def runAsyncPureProducesRightIO[A](a: A) = {
    val fa = monadError.pure(a)
    var result: Option[Either[E, A]] = None
    val read = ev delay { result.get }

    ev.runAsync(fa)(e => ev.delay { result = Some(e) }) *> read <-> ev.pure(a.asRight)
  }

  def runAsyncRaiseErrorProducesLeftIO[A](e: E) = {
    val fa: F[A] = monadError.raiseError(e)
    var result: Option[Either[E, A]] = None
    val read = ev delay { result.get }

    ev.runAsync(fa)(e => ev.delay { result = Some(e) }) *> read <-> ev.pure(e.asLeft)
  }

  def runAsyncIgnoresErrorInHandler[A](e: E) = {
    val fa = monadError.pure(())
    ev.runAsync(fa)(_ => monadError.raiseError(e)) <-> ev.pure(())
  }

  def repeatedCallbackIgnored[A](a: A, f: A => A) = {
    var cur = a
    val change = ev delay { cur = f(cur) }
    val readResult = ev delay cur

    val double: F[Unit] = ev async { cb =>
      cb(Right(()))
      cb(Right(()))
    }

    val test = ev.runAsync(double *> change) { _ => ev.unit }

    test *> readResult <-> ev.pure(f(a))
  }

  /**
    * Laws from cats.effect Async[F]
    */
  def asyncRightIsPure[A](a: A) =
    ev.async[A](_(Right(a))) <-> monadError.pure(a)

  def asyncLeftIsRaiseError[A](e: E) =
    ev.async[A](_(Left(e))) <-> monadError.raiseError(e)

  def repeatedAsyncEvaluationNotMemoized[A](a: A, f: A => A) = {
    var cur = a

    val change: F[Unit] = ev async { cb =>
      cur = f(cur)
      cb(Right(()))
    }

    val read: F[A] = ev.delay(cur)

    change *> change *> read <-> monadError.pure(f(f(a)))
  }

  def propagateErrorsThroughBindAsync[A](t: E) = {
    val fa = monadError.attempt(monadError.flatMap(ev.async[A](_ (Left(t))))(x => monadError.pure(x)))

    fa <-> monadError.pure(Left(t))
  }

  /**
    * Laws from cats.effect Sync[F]
    */
  def delayConstantIsPure[A](a: A) =
    ev.delay(a) <-> monadError.pure(a)

  def suspendConstantIsPureJoin[A](fa: F[A]) =
    ev.suspend(fa) <-> monadError.flatten(monadError.pure(fa))

  def delayThrowIsRaiseError[A](e: E) =
    ev.delay[A](throw e) <-> monadError.raiseError(e)

  def suspendThrowIsRaiseError[A](e: E) =
    ev.suspend[A](throw e) <-> monadError.raiseError(e)

  def unsequencedDelayIsNoop[A](a: A, f: A => A) = {
    var cur = a
    val change = ev delay { cur = f(cur) }
    val _ = change

    ev.delay(cur) <-> monadError.pure(a)
  }

  def repeatedSyncEvaluationNotMemoized[A](a: A, f: A => A) = {
    var cur = a
    val change = ev delay { cur = f(cur) }
    val read = ev.delay(cur)

    change *> change *> read <-> monadError.pure(f(f(a)))
  }

  def propagateErrorsThroughBindSuspend[A](t: E) = {
    val fa = monadError.flatMap(ev.delay[A](throw t))(x => monadError.pure(x))

    fa <-> monadError.raiseError(t)
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
    val result = (0 until 10000).foldLeft(ev.delay(())) { (acc, _) =>
      monadError.flatMap(acc)(_ => ev.delay(()))
    }

    result <-> monadError.pure(())
  }

  def stackSafetyOnRepeatedRightBinds = {
    val result = (0 until 10000).foldRight(ev.delay(())) { (_, acc) =>
      monadError.flatMap(ev.delay(()))(_ => acc)
    }

    result <-> monadError.pure(())
  }

  def stackSafetyOnRepeatedAttempts = {
    // Note this isn't enough to guarantee stack safety, unless
    // coupled with `bindSuspendsEvaluation`
    val result = (0 until 10000).foldLeft(ev.delay(())) { (acc, _) =>
      monadError.map(monadError.attempt(acc))(_ => ())
    }
    result <-> monadError.pure(())
  }

  def stackSafetyOnRepeatedMaps = {
    // Note this isn't enough to guarantee stack safety, unless
    // coupled with `mapSuspendsEvaluation`
    val result = (0 until 10000).foldLeft(ev.delay(0)) { (acc, _) =>
      monadError.map(acc)(_ + 1)
    }
    result <-> monadError.pure(10000)
  }
}

object EffectfulLaws {

  @inline def apply[E, F[_]](implicit ev1: Effectful[E, F], ev2: MonadError[F, E]): EffectfulLaws[E, F] =
    new EffectfulLaws[E, F] {
      def ev: Effectful[E, F] = ev1
      def monadError: MonadError[F, E] = ev2
    }
}
