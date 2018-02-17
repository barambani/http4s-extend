package http4s.extend.laws

import cats.effect.IO
import cats.laws._
import cats.syntax.apply._
import http4s.extend.Effectful

/**
  * Effectful, together with its MonadError evidence, should abide by the same laws as cats.effect.Effect
  * The code below comes straight from Cats.Effect and hopefully is temporary.
  */
sealed trait EffectfulLaws[F[_]] {

  implicit def ev: Effectful[F]
  implicit def monadError = ev.monadError

  /**
    * Laws from cats.effect Effect[F]
    */
  def runAsyncPureProducesRightIO[A](a: A) = {
    val fa = monadError.pure(a)
    var result: Option[Either[Throwable, A]] = None
    val read = IO { result.get }

    ev.runAsync(fa)(e => IO { result = Some(e) }) *> read <-> IO.pure(Right(a))
  }

  def runAsyncRaiseErrorProducesLeftIO[A](e: Throwable) = {
    val fa: F[A] = monadError.raiseError(e)
    var result: Option[Either[Throwable, A]] = None
    val read = IO { result.get }

    ev.runAsync(fa)(e => IO { result = Some(e) }) *> read <-> IO.pure(Left(e))
  }

  def runAsyncIgnoresErrorInHandler[A](e: Throwable) = {
    val fa = monadError.pure(())
    ev.runAsync(fa)(_ => IO.raiseError(e)) <-> IO.pure(())
  }

  def repeatedCallbackIgnored[A](a: A, f: A => A) = {
    var cur = a
    val change = ev delay { cur = f(cur) }
    val readResult = IO { cur }

    val double: F[Unit] = ev async { cb =>
      cb(Right(()))
      cb(Right(()))
    }

    val test = ev.runAsync(double *> change) { _ => IO.unit }

    test *> readResult <-> IO.pure(f(a))
  }

  /**
    * Laws from cats.effect Async[F]
    */
  def asyncRightIsPure[A](a: A) =
    ev.async[A](_(Right(a))) <-> monadError.pure(a)

  def asyncLeftIsRaiseError[A](e: Throwable) =
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

  def propagateErrorsThroughBindAsync[A](t: Throwable) = {
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

  def delayThrowIsRaiseError[A](e: Throwable) =
    ev.delay[A](throw e) <-> monadError.raiseError(e)

  def suspendThrowIsRaiseError[A](e: Throwable) =
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

  def propagateErrorsThroughBindSuspend[A](t: Throwable) = {
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

  @inline def apply[F[_]](implicit iev: Effectful[F]): EffectfulLaws[F] =
    new EffectfulLaws[F] {
      def ev: Effectful[F] = iev
    }
}