ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "io.forward"

lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion = "2.5.21"

lazy val root = (project in file("."))
  .settings(
    name := "switch-core",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion
    )
  )
