# Http4s Extend
[![Build Status](https://travis-ci.org/barambani/http4s-extend.svg?branch=master)](https://travis-ci.org/barambani/http4s-extend)
[![codecov](https://codecov.io/gh/barambani/http4s-extend/branch/master/graph/badge.svg)](https://codecov.io/gh/barambani/http4s-extend)
[![scalaindex](https://img.shields.io/badge/scalaindex-http4s--extend-orange.svg)](https://index.scala-lang.org/barambani/http4s-extend/http4s-extend)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.11.svg?label=central%20repo%202.11&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.12.svg?label=central%20repo%202.12&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.12)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/barambani/http4s-extend/blob/master/LICENSE)

A basic, still descriptive, example that demonstrates some possible uses for Http4s Extend can be found [here](https://github.com/barambani/http4s-poc-api)

**Note:** this project is in early alpha stage. Not usable in production.

### Using Http4s Extend
Http4s Extend is available for Scala `2.11.x` and `2.12.x`. To use it create the dependency by adding the following to the sbt build
```scala
libraryDependencies += "com.github.barambani" %% "http4s-extend" % "0.0.21"
```
a sample configuration can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/build.sbt) where the dependencies are taken from [here](https://github.com/barambani/http4s-poc-api/blob/master/project/Dependencies.scala)

### Dependencies
For its own purpose is integration, Http4s Extend will bring into a using project some dependencies. Those are:

|        | Http4s    | Monix    | Scalaz Concurrent |
| ------ |:---------:|:--------:|:-----------------:|
| 0.0.21 | 0.18.2    | 3.0.0-M3 | 7.2.20            |
