name := "scala-cqrs"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq(
  "-feature")

publish := {}

lazy val api = project
lazy val core = project.dependsOn(api)
lazy val memory = project.dependsOn(api, core)
lazy val postgres = project.dependsOn(api, core)