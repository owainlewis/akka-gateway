ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "io.forward"

lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion = "2.5.21"

lazy val commonSettings = Seq(
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  publishMavenStyle := true
)

lazy val root =
  (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "switch-core",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"    % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"  % akkaVersion,
      "org.typelevel"     %% "cats-core"    % "1.6.0",
      "io.circe"          %% "circe-core"   % "0.11.1"
    )
  )
