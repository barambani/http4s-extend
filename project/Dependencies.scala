import sbt._

object Dependencies {

  private val http4sVersion     = "0.18.1"
  private val monixVersion      = "3.0.0-M3"
  private val scalazVersion     = "7.2.20"
  private val catsLawsVersion   = "1.0.1"

  private val catsTestKitVersion  = "1.0.1"
  private val scalaCheckVersion   = "1.13.5"
  private val catsEffectLaws      = "0.9"

  private val kindProjectorVersion = "0.9.6"

  val externalDependencies: Seq[ModuleID] = Seq(
    "org.http4s"          %% "http4s-dsl"           % http4sVersion   withSources(),
    "org.http4s"          %% "http4s-blaze-server"  % http4sVersion   withSources(),
    "org.http4s"          %% "http4s-blaze-client"  % http4sVersion   withSources(),
    "org.http4s"          %% "http4s-circe"         % http4sVersion   withSources(),
    "io.monix"            %% "monix"                % monixVersion    withSources(),
    "org.scalaz"          %% "scalaz-concurrent"    % scalazVersion   withSources(),
    "org.typelevel"       %% "cats-laws"            % catsLawsVersion withSources()
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "org.typelevel"   %% "cats-testkit"     % catsTestKitVersion  % "test" withSources(),
    "org.scalacheck"  %% "scalacheck"       % scalaCheckVersion   % "test" withSources(),
    "org.typelevel"   %% "cats-effect-laws" % catsEffectLaws      % "test" withSources()
  )

  val compilerPlugins: Seq[ModuleID] = Seq(
    compilerPlugin("org.spire-math" %% "kind-projector" % kindProjectorVersion)
  )
}