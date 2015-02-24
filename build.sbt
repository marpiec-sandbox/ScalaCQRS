name := "scala-cqrs"

organization := "pl.mpieciukiewicz"

version := "0.1.3-SNAPSHOT"

scalaVersion := "2.11.5"

scalacOptions ++= Seq(
  "-feature")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := Some("snapshots" at sys.props.getOrElse("mavenRepo", default = ""))

publishLocal := {}

lazy val api = project
lazy val core = project.dependsOn(api)
lazy val memory = project.dependsOn(api, core)
lazy val postgres = project.dependsOn(api, core)
lazy val testdomain = project.dependsOn(api, core, memory, postgres)