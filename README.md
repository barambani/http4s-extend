# Http4s Extend
[![Build Status](https://travis-ci.org/barambani/http4s-extend.svg?branch=master)](https://travis-ci.org/barambani/http4s-extend)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.barambani/http4s-extend_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.barambani/http4s-extend_2.12)

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
libraryDependencies += "com.github.barambani" %% "http4s-extend" % "0.0.4"
```
**Note:** a sample configuration can be found [here](https://github.com/barambani/http4s-poc-api/blob/master/build.sbt) where the dependencies are taken from [here](https://github.com/barambani/http4s-poc-api/blob/master/project/Dependencies.scala)
