name := "scala-cqrs"

organization := "io.scalacqrs"

version := "0.3.2-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-feature")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := Some("snapshots" at sys.props.getOrElse("snapshotsRepo", default = "http://someMockRepo.com"))

publishLocal := {}

lazy val api = project
lazy val core = project.dependsOn(api)
lazy val memory = project.dependsOn(api, core)
lazy val postgres = project.dependsOn(api, core)
lazy val testdomain = project.dependsOn(api, core, memory, postgres)