name := "scala-cqrs"

version := "1.0.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq(
  "-feature")

lazy val api = (project in file("api")).settings(
  name := "scala-cqrs-api",
  version := "1.0.0",
  scalaVersion := "2.11.4"
)

lazy val memory = (project in file("memory-impl")).dependsOn(api).settings(
  name := "scala-cqrs-memory",
  version := "1.0.0",
  scalaVersion := "2.11.4",
  libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.7",
                              "org.scalatest" %% "scalatest" % "2.2.2" % Test))