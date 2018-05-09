import sbt._

object Dependencies {

  private[Dependencies] object versionOf {
    val http4s    = "0.18.10"
    val monix     = "3.0.0-RC1"
    val scalaz    = "7.2.22"
    val cats      = "1.1.0"
    val shapeless = "2.3.3"

    val scalaCheck      = "1.13.5"
    val catsEffectLaws  = "1.0.0-RC"

    val kindProjector = "0.9.6"
  }

  val externalDependencies: Seq[ModuleID] = Seq(
    "org.http4s"    %% "http4s-dsl"           % versionOf.http4s,
    "org.http4s"    %% "http4s-blaze-server"  % versionOf.http4s,
    "org.http4s"    %% "http4s-blaze-client"  % versionOf.http4s,
    "org.http4s"    %% "http4s-circe"         % versionOf.http4s,
    "io.monix"      %% "monix"                % versionOf.monix,
    "org.scalaz"    %% "scalaz-concurrent"    % versionOf.scalaz,
    "org.typelevel" %% "cats-laws"            % versionOf.cats,
    "com.chuusai"   %% "shapeless"            % versionOf.shapeless
  ) map (_.withSources)

  val testDependencies: Seq[ModuleID] = Seq(
    "org.typelevel"   %% "cats-testkit"     % versionOf.cats            % "test",
    "org.scalacheck"  %% "scalacheck"       % versionOf.scalaCheck      % "test",
    "org.typelevel"   %% "cats-effect-laws" % versionOf.catsEffectLaws  % "test"
  )

  val compilerPlugins: Seq[ModuleID] = Seq(
    compilerPlugin("org.spire-math" %% "kind-projector" % versionOf.kindProjector cross CrossVersion.binary),
    compilerPlugin("io.tryp"        %   "splain"        % "0.2.9"                 cross CrossVersion.patch)
  )
}