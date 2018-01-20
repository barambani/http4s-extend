# Http4s Extend
[![Build Status](https://travis-ci.org/barambani/http4s-extend.svg?branch=master)](https://travis-ci.org/barambani/http4s-extend)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.12)

Http4s Extend is a small set of integration tools to help building http4s api that depend on third party libraries. The main features are:
* facilitate the transformation of dependencies' abstractions for effectful computations
* provide some tools for decoupling the error type of http4s services' `MonadError` from `Throwable` allowing not to fix it in modules where there is no need for that
* provide some helper modules for tests that are implemented over the `Either` Monad instead of `IO`.

A basic, still descriptive, example that demonstrates some possible uses for Http4s Extend can be found [here](https://github.com/barambani/http4s-poc-api)  

### Using Http4s Extend
Http4s Extend is available for Scala `2.11.x` and `2.12.x`. To have it as a dependency add the Sonatype Repo to the project's settings resolvers as below
```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
```
and then create the dependency by adding the following to the build
```scala
libraryDependencies += "com.github.barambani" %% "http4s-extend" % "0.0.7"
```
**Note:** a sample configuration can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/build.sbt) where the dependencies are taken from [here](https://github.com/barambani/http4s-poc-api/blob/master/project/Dependencies.scala)

### Dependencies ###
For its own purpose is integration, Http4s Extend will bring into a using project some dependencies. Those are:

|       | Http4s    | Monix    | Scalaz Concurrent | Cats (from Http4s) | Cats Effect (from Monix) | Circe (from Http4s) |
| ----- |:---------:|:--------:|:-----------------:|:------------------:| :-----------------------:|:-------------------:|
| 0.0.7 | 0.18.0-M8 | 3.0.0-M3 | 7.2.18            | 1.0.1              | 0.8                      | 0.9.0               |
