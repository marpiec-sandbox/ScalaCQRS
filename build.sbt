name := "scala-cqrs"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq(
  "-feature")


lazy val api = project

lazy val memory = project.dependsOn(api)