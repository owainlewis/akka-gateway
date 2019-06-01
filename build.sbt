///////////////////////////////////////
// Versions
///////////////////////////////////////
lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion = "2.5.21"
lazy val circeVersion = "0.11.1"
lazy val catsVersion = "1.6.0"
lazy val awsSDKVersion = "1.11.542"
///////////////////////////////////////
// Scopes
///////////////////////////////////////
lazy val testScope = "test"
///////////////////////////////////////
// Projects
///////////////////////////////////////
lazy val commonSettings = Seq(
  organization := "io.forward",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.0"
)

lazy val examples =
  (project in file("examples"))
    .dependsOn(core, aws, auth)
    .settings(commonSettings: _*)
    .settings(
      name := "gateway-example",
      libraryDependencies ++= Seq(
    )
  )

lazy val aws =
  (project in file("gateway-aws"))
    .dependsOn(core)
    .settings(commonSettings: _*)
    .settings(
      name := "gateway-aws",
      libraryDependencies ++= Seq(
        "com.amazonaws" % "aws-java-sdk" % "1.11.542"
      )
    )

lazy val core =
  (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "gateway-core",
    publishMavenStyle := true,
    libraryDependencies ++= Seq(
      "com.typesafe.akka"  %% "akka-http"           % akkaHttpVersion,
      "com.typesafe.akka"  %% "akka-actor"          % akkaVersion,
      "com.typesafe.akka"  %% "akka-stream"         % akkaVersion,
      "org.typelevel"      %% "cats-core"           % catsVersion,
      "io.circe"           %% "circe-core"          % circeVersion,
      "io.circe"           %% "circe-generic"       % circeVersion,
      "org.scalatest"      %% "scalatest"           % "3.0.5"         % testScope,
      "com.typesafe.akka"  %% "akka-http-testkit"   % akkaHttpVersion % testScope,
      "com.typesafe.akka"  %% "akka-stream-testkit" % akkaVersion     % testScope
    )
  )
