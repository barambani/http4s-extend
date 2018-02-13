# Http4s Extend
[![Build Status](https://travis-ci.org/barambani/http4s-extend.svg?branch=master)](https://travis-ci.org/barambani/http4s-extend)
[![codecov](https://codecov.io/gh/barambani/http4s-extend/branch/master/graph/badge.svg)](https://codecov.io/gh/barambani/http4s-extend)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.11.svg?label=version%20for%202.11)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.12.svg?label=version%20for%202.12)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.12)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/barambani/http4s-extend/blob/master/LICENSE)

Http4s Extend is a small set of integration tools to help building http4s api that depend on third party libraries. The main features are:
* facilitate the transformation of dependencies' abstractions for effectful computations
* provide some tools for decoupling the error type of http4s services' `MonadError` from `Throwable` allowing not to fix it in modules where there is no need for that
* provide some helper modules for tests that are implemented over the `Either` Monad instead of `IO`.

A basic, still descriptive, example that demonstrates some possible uses for Http4s Extend can be found [here](https://github.com/barambani/http4s-poc-api)

**Note:** this project is in early alpha stage. Not usable in production.

### Using Http4s Extend
Http4s Extend is available for Scala `2.11.x` and `2.12.x`. To use it create the dependency by adding the following to the sbt build
```scala
libraryDependencies += "com.github.barambani" %% "http4s-extend" % "0.0.15"
```
**Note:** a sample configuration can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/build.sbt) where the dependencies are taken from [here](https://github.com/barambani/http4s-poc-api/blob/master/project/Dependencies.scala)

### Dependencies ###
For its own purpose is integration, Http4s Extend will bring into a using project some dependencies. Those are:

|        | Http4s    | Monix    | Scalaz Concurrent |
| ------ |:---------:|:--------:|:-----------------:|
| 0.0.15 | 0.18.0-M9 | 3.0.0-M3 | 7.2.18            |
