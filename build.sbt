lazy val akkaHttpVersion = "10.1.8"

lazy val akkaVersion = "2.5.21"

lazy val commonSettings = Seq(
  organization := "io.forward",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.0"
)

lazy val examples =
  (project in file("examples"))
    .dependsOn(core)
    .settings(commonSettings: _*)
    .settings(
      name := "switch-example",
      libraryDependencies ++= Seq(
    )
  )

lazy val core =
  (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "switch-core",
    publishMavenStyle := true,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"    % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"  % akkaVersion,
      "org.typelevel"     %% "cats-core"    % "1.6.0",
      "io.circe"          %% "circe-core"   % "0.11.1"
    )
  )

mainClass in run := Some("examples.Application")
