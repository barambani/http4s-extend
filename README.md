# Http4s Extend
[![Build Status](https://travis-ci.org/barambani/http4s-extend.svg?branch=master)](https://travis-ci.org/barambani/http4s-extend)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.12)

Http4s Extend is a small set of integration tools to help building http4s api that depend on third party libraries. It facilitates the transformation of the dependencies' abstraction for effectful computations, it provides some tools for decoupling the error type of the http4s services' `MonadError` from `Throwable` allowing not to fix it in modules where there is no need for that and it gives some help when the tests for the api's services need to be implemented over `Either` instead of an `IO` monad. A basic, still descriptive, example use case for it can be found [here](https://github.com/barambani/http4s-poc-api)  

### Using Http4s Extend
To add Http4s Extend as a dependency add the Sonatype Repo to the project's settings resolvers like in
```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
```
and then create the dependency adding the following to the build
```scala
libraryDependencies += "com.github.barambani" %% "http4s-extend" % "0.0.5"
```
**Note:** a sample configuration can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/build.sbt) where the dependencies are taken from [here](https://github.com/barambani/http4s-poc-api/blob/master/project/Dependencies.scala)
