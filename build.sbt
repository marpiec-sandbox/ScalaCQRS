name := "scala-cqrs"

organization := "pl.mpieciukiewicz"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.5"

scalacOptions ++= Seq(
  "-feature")

publish := {}

publishLocal := {}

lazy val api = project
lazy val core = project.dependsOn(api)
lazy val memory = project.dependsOn(api, core)
lazy val postgres = project.dependsOn(api, core)
lazy val testdomain = project.dependsOn(api, core, memory, postgres)