name := "core"

organization := "io.scalacqrs"

version := "0.2.4-SNAPSHOT"

scalaVersion := "2.11.5"

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := Some("snapshots" at sys.props.getOrElse("snapshotsRepo", default = "http://someMockRepo.com"))

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.7",
  "org.scalatest" %% "scalatest" % "2.2.2" % Test)