import sbt._

object Dependencies {

  private val http4sVersion     = "0.18.0-M8"
  private val monixVersion      = "3.0.0-M3"
  private val scalazVersion     = "7.2.18"
  private val scalaCheckVersion = "1.13.5"

  private val kindProjectorVersion = "0.9.4"

  val externalDependencies: Seq[ModuleID] = Seq(
    "org.http4s"          %% "http4s-dsl"           % http4sVersion withSources(),
    "org.http4s"          %% "http4s-blaze-server"  % http4sVersion withSources(),
    "org.http4s"          %% "http4s-blaze-client"  % http4sVersion withSources(),
    "org.http4s"          %% "http4s-circe"         % http4sVersion withSources(),
    "io.monix"            %% "monix"                % monixVersion  withSources(),
    "org.scalaz"          %% "scalaz-concurrent"    % scalazVersion withSources()
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test" withSources()
  )

  val compilerPlugins: Seq[ModuleID] = Seq(
    compilerPlugin("org.spire-math" %% "kind-projector" % kindProjectorVersion)
  )
}