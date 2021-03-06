import sbt._

object Dependencies {

  private[Dependencies] object versionOf {
    val http4s      = "0.20.10"
    val monix       = "3.0.0-RC1"
    val scalaz      = "7.3.0-M27"
    val cats        = "1.6.1"
    val catsEffect  = "1.4.0"
    val shapeless   = "2.3.3"

    val scalaCheck      = "1.13.5"

    val kindProjector = "0.10.3"
    val splain        = "0.4.1"
    val silencer      = "1.4.2"
  }

  val externalDependencies: Seq[ModuleID] = Seq(
    "org.http4s"      %% "http4s-dsl"           % versionOf.http4s,
    "org.http4s"      %% "http4s-blaze-server"  % versionOf.http4s,
    "org.http4s"      %% "http4s-blaze-client"  % versionOf.http4s,
    "org.http4s"      %% "http4s-circe"         % versionOf.http4s,
    "io.monix"        %% "monix"                % versionOf.monix,
    "org.scalaz"      %% "scalaz-concurrent"    % versionOf.scalaz,
    "org.typelevel"   %% "cats-laws"            % versionOf.cats,
    "com.chuusai"     %% "shapeless"            % versionOf.shapeless,
    "com.github.ghik" %% "silencer-lib"         % versionOf.silencer % Provided
  ) map (_.withSources)

  val testDependencies: Seq[ModuleID] = Seq(
    "org.typelevel"   %% "cats-testkit"     % versionOf.cats        % "test",
    "org.scalacheck"  %% "scalacheck"       % versionOf.scalaCheck  % "test",
    "org.typelevel"   %% "cats-effect-laws" % versionOf.catsEffect  % "test"
  )

  val compilerPlugins: Seq[ModuleID] = Seq(
    compilerPlugin("org.typelevel"   %% "kind-projector"   % versionOf.kindProjector cross CrossVersion.binary),
    compilerPlugin("com.github.ghik"  %% "silencer-plugin"  % versionOf.silencer),
    compilerPlugin("io.tryp"          %   "splain"          % versionOf.splain        cross CrossVersion.patch)
  )
}
