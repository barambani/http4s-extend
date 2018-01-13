import Dependencies._
import ScalacOptions._

val typelevelOrganization = "org.typelevel"
val globalOrganization = scalaOrganization in Global

val scala_typelevel_212 = "2.12.4-bin-typelevel-4"
val scala_211 = "2.11.11"
val scala_212 = "2.12.4"

val crossBuildSettings: Seq[Def.Setting[_]] = Seq(
  scalacOptions           ++= crossBuildOptions,
  crossScalaVersions 	    :=  Seq(scala_211, scala_212),
  scalaOrganization :=
    (scalaVersion.value match {
      case `scala_typelevel_212` => typelevelOrganization
      case _                   => globalOrganization.value
    }),
  scalacOptions ++=
    (scalaVersion.value match {
      case `scala_212`            => scala212Options
      case `scala_typelevel_212`  => scala212Options ++ typeLevelScalaOptions
      case _                      => Seq()
    })
)

val releaseSettings: Seq[Def.Setting[_]] = Seq(
  releaseCrossBuild             := true,
  publishMavenStyle             := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishArtifact in Test       := false,
  pomIncludeRepository          := { _ => false },
  licenses                      := Seq("MIT License" -> url("https://raw.githubusercontent.com/barambani/http4s-extend/master/LICENSE")),
  homepage                      := Some(url("https://github.com/barambani/http4s-extend")),
  publishTo                     := {
    if (isSnapshot.value) Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
    else                  Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  },
  pomExtra :=
    <scm>
      <url>https://github.com/barambani/http4s-extend</url>
      <connection>scm:git:git@github.com:barambani/http4s-extend.git</connection>
    </scm>
    <developers>
      <developer>
        <id>barambani</id>
        <name>Filippo Mariotti</name>
        <url>https://github.com/barambani</url>
      </developer>
    </developers>
)

val root = project.in(file("."))
  .settings(crossBuildSettings)
  .settings(releaseSettings)
  .settings(
    name 	                  :=  "http4s-extend",
    scalaVersion            :=  scala_typelevel_212,
    libraryDependencies     ++= externalDependencies,
    scalacOptions in Test   ++= testOnlyOptions,
    scalacOptions in (Compile, console) --= nonTestExceptions,
    resolvers               +=  Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
  )