# Http4s Extend
[![Build Status](https://travis-ci.org/barambani/http4s-extend.svg?branch=master)](https://travis-ci.org/barambani/http4s-extend)
[![codecov](https://codecov.io/gh/barambani/http4s-extend/branch/master/graph/badge.svg)](https://codecov.io/gh/barambani/http4s-extend)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.11.svg?label=version%20for%202.11)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.12.svg?label=version%20for%202.12)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.12)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/barambani/http4s-extend/blob/master/LICENSE)

Http4s Extend is a small set of integration tools to help building http4s api that depend on third party libraries. The main features are:
* facilitate the transformation of dependencies' abstractions for effectful computations
* provide an easy to use abstraction for parallel execution of computaions based on fs2 async. This will probably be removed as soon as the `IO` instance for the *cats* `Parallel` type class will be available to the public
* provide some tools to decouple the error type of http4s services' `MonadError` from `Throwable` allowing not to fix it in modules where there is no need for that
* provide some helper modules for tests that are implemented over the `Either` Monad instead of `IO`.

A basic, still descriptive, example that demonstrates some possible uses for Http4s Extend can be found [here](https://github.com/barambani/http4s-poc-api)

**Note:** this project is in early alpha stage. Not usable in production.

### Using Http4s Extend
Http4s Extend is available for Scala `2.11.x` and `2.12.x`. To use it create the dependency by adding the following to the sbt build
```scala
libraryDependencies += "com.github.barambani" %% "http4s-extend" % "0.0.18"
```
a sample configuration can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/build.sbt) where the dependencies are taken from [here](https://github.com/barambani/http4s-poc-api/blob/master/project/Dependencies.scala)

### Dependencies
For its own purpose is integration, Http4s Extend will bring into a using project some dependencies. Those are:

|        | Http4s    | Monix    | Scalaz Concurrent |
| ------ |:---------:|:--------:|:-----------------:|
| 0.0.18 | 0.18.0    | 3.0.0-M3 | 7.2.19            |

## Examples
### Parallel execution
When sections of the computation can be completed in parallel `parTupled2` or `parTupled3` may help. They are defined in the `ParEffectful[F[_]]` type class in term of `parMap2` as
```scala
trait ParEffectful[F[_]] {

  val semigroupalEvidence: Semigroupal[F]

  def parMap2[A, B, R](fa: F[A], fb: F[B])(f: (A, B) => R): F[R]

  def parMap3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) => R): F[R] =
    parMap2(fa, semigroupalEvidence.product(fb, fc))((a, b) => f(a, b._1, b._2))

  def parTupled2[A, B, R](fa: F[A], fb: F[B]): F[(A, B)] =
    parMap2(fa, fb)(Tuple2.apply)

  def parTupled3[A, B, C, R](fa: F[A], fb: F[B], fc: F[C]): F[(A, B, C)] =
    parMap3(fa, fb, fc)(Tuple3.apply)
}
```
and they model parallel tupling in the context `F[_]`. The `cats.effect.IO` instance provided for them will run the
effects using `fs2.async`
```scala
val composedApply = Apply[IO] compose Apply[IO]

def parMap2[A, B, R](fa: IO[A], fb: IO[B])(f: (A, B) => R): IO[R] =
  composedApply.map2(fs2.async.start(fa), fs2.async.start(fb))(f) flatMap identity
```
this will make code like the one below easy to write without incurring in the *ambiguous implicits* problem that afflicts the `cats.effect.Effect[F[_]]` hierarchy when used with other *monads* in *Mtl* style. Notice in fact that we are already requiring `F[_] : MonadError[?[_], ApiError]` so requiring also an `Effect[F]` evidence needed by `fs2.async` will brake the compilation because they will both try to bring in instances for `MonadError` and all the supertypes. The trick here is that we give up on the modelling power of `Effect[F]` and we just provide evidence that we can run things in parallel delegating the safety of the computation to the implementation (and this is far from being ideal, but `Effect` cannot take care of it anyway at the moment). We will need `Effect` only when we will need to materialize an instance for `ParEffectful` (`ParEffectful[IO]` in this case).
```scala

final case class PriceService[F[_] : MonadError[?[_], ApiError]](dep: Dependencies[F], logger: Logger[F])(implicit ev: ParEffectful[F]) {

  def userFor(userId: UserId): F[User] = ???
  def preferencesFor(userId: UserId): F[UserPreferences] = ???
  def productsFor(productIds: Seq[ProductId]): F[List[Product]] = ???

  def prices(userId: UserId, productIds: Seq[ProductId]): F[Seq[Price]] =
    for {
      retrievalResult               <- ev.parTupled3(userFor(userId), productsFor(productIds), preferencesFor(userId))
      (user, products, preferences) =  retrievalResult
      productsPrices                <- priceCalculator.finalPrices(user, products, preferences)
    } yield productsPrices
}
```
A full running exmple of the above can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/src/main/scala/service/PriceService.scala#L12).

**Note:** as soon as the `cats.effect.IO` instance for `cats.Parallel` will be available to the public this code will probably change as it should provide another solution to the problem being independent from `Effect`.

### MonadError error adapter
There are cases when doesn't make a lot of sense to model failures with `Throwable`. Sometimes it might just be more accurate to define a custom hierarchy that describe them speaking the language of the domain at hand and possibly fixes this language in the types. This might be especially true in the core of the api where the computation effects and the *impure* details of the interaction with the runtime are still abstracted over. There is really no place for throwable things there. `MonadError` already provides an abstraction to express the fact that a context `F[_]` can possibly be in a failing state. Unfortunately though, whenever `MonadError` is used with `cats.effect.IO` the error type is fixed to `Throwable` and there is no easy way to translate it to something else. In such cases something like `adaptErrorType` could be used. It is defined as
```scala
def adaptErrorType[F[_], E1, E2](me: MonadError[F, E1])(implicit EC: ErrorInvariantMap[E1, E2]): MonadError[F, E2] =
  new MonadError[F, E2] {
    def raiseError[A](e: E2): F[A] =
      (me.raiseError[A] _ compose EC.reverse)(e)

    def handleErrorWith[A](fa: F[A])(f: E2 => F[A]): F[A] =
      me.handleErrorWith(fa)(f compose EC.direct)

    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] =
      me.flatMap(fa)(f)

    def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B] =
      me.tailRecM(a)(f)

    def pure[A](x: A): F[A] =
      me.pure(x)
  }
```
As can be noticed, this definition says that `adaptErrorType` can adapt the error as long as it exists a way to map `E1` to `E2` and back. This existence is expressed by the evidence of an `ErrorInvariantMap[E1, E2]` that is defined as
```scala
trait ErrorInvariantMap[E1, E2] {
  def direct: E1 => E2
  def reverse: E2 => E1
}
```
An example of how to use `adaptErrorType` could be
```scala
def ioApiError[E](implicit ev: ErrorInvariantMap[Throwable, E]): MonadError[IO, E] =
  MonadError[IO, Throwable].adaptErrorType[E]
```
where an instance of `MonadError[IO, E]` is created adapting from `MonadError[IO, Throwable]`. The error map between the two could be written as
```scala
def throwableToApiError(implicit ev: Invariant[ErrorInvariantMap[Throwable, ?]]): ErrorInvariantMap[Throwable, ApiError] =
  ErrorInvariantMap[Throwable, ExceptionDisplay].imap[ApiError](UnknownFailure.apply)(ae => ExceptionDisplay.mk(ae.message))
```
where an instance for `Invariant[ErrorInvariantMap[Throwable, ?]]` is provided and is defined as
```scala
implicit def errorMapInvariant[E]: Invariant[ErrorInvariantMap[E, ?]] =
  new Invariant[ErrorInvariantMap[E, ?]] {

    def imap[A, B](fa: ErrorInvariantMap[E, A])(f: A => B)(g: B => A): ErrorInvariantMap[E, B] =
      new ErrorInvariantMap[E, B] {
        def direct: E => B =
          f compose fa.direct

        def reverse: B => E =
          fa.reverse compose g
      }
  }
```
Please see [here](https://github.com/barambani/http4s-poc-api/blob/master/src/main/scala/errors/Errors.scala#L17) for a complete working example together with a possible error hierarchy. In the example the type `ExceptionDisplay` and the `ThrowableModule` util are also shown. They are not needed for the mechanism to work, they just help in dumping a full stack of nested `Throwable` into a `String` and back.
