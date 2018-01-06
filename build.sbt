import Dependencies._
import ScalacOptions._

lazy val typelevelOrganization = "org.typelevel"
lazy val globalOrganization = scalaOrganization in Global

lazy val Scala_typelevel_212 = "2.12.4-bin-typelevel-4"
lazy val Scala_211 = "2.11.11"
lazy val Scala_212 = "2.12.4"

lazy val root = project.in(file("."))
  .settings(
    version 	          := "0.0.1",
    name 	          := "http4s-extend",
    scalaOrganization     := 
      (scalaVersion.value match {
        case Scala_typelevel_212 => typelevelOrganization
        case _                   => globalOrganization.value
      }),
    scalaVersion          := Scala_typelevel_212,
    coverageMinimum       := 85,
    coverageFailOnMinimum := false,
    crossScalaVersions 	  := Seq(Scala_211, Scala_212),
    resolvers             += Resolver.sonatypeRepo("releases"),
    libraryDependencies   ++= externalDependencies,
    scalacOptions         ++= crossBuildOptions,
    scalacOptions in Test ++= testOnlyOptions,
    scalacOptions         ++=
      (scalaVersion.value match {
      	case Scala_212           => scala212Options
        case Scala_typelevel_212 => (scala212Options ++ typeLevelScalaOptions)
        case _                   => Seq()
      }),
    scalacOptions in (Compile, console) --= nonTestExceptions,
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
  )
