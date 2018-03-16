import sbt._

object Dependencies {

  object versionOf {
    private[Dependencies] val http4s   = "0.18.2"
    private[Dependencies] val monix    = "3.0.0-M3"
    private[Dependencies] val scalaz   = "7.2.20"
    private[Dependencies] val catsLaws = "1.0.1"

    private[Dependencies] val catsTestKit     = "1.0.1"
    private[Dependencies] val scalaCheck      = "1.13.5"
    private[Dependencies] val catsEffectLaws  = "0.10"

    private[Dependencies] val kindProjector = "0.9.6"
  }

  val externalDependencies: Seq[ModuleID] = Seq(
    "org.http4s"    %% "http4s-dsl"           % versionOf.http4s   withSources(),
    "org.http4s"    %% "http4s-blaze-server"  % versionOf.http4s   withSources(),
    "org.http4s"    %% "http4s-blaze-client"  % versionOf.http4s   withSources(),
    "org.http4s"    %% "http4s-circe"         % versionOf.http4s   withSources(),
    "io.monix"      %% "monix"                % versionOf.monix    withSources(),
    "org.scalaz"    %% "scalaz-concurrent"    % versionOf.scalaz   withSources(),
    "org.typelevel" %% "cats-laws"            % versionOf.catsLaws withSources()
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "org.typelevel"   %% "cats-testkit"     % versionOf.catsTestKit  % "test" withSources(),
    "org.scalacheck"  %% "scalacheck"       % versionOf.scalaCheck   % "test" withSources(),
    "org.typelevel"   %% "cats-effect-laws" % versionOf.catsEffectLaws      % "test" withSources()
  )

  val compilerPlugins: Seq[ModuleID] = Seq(
    compilerPlugin("org.spire-math" %% "kind-projector" % versionOf.kindProjector)
  )
}