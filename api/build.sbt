name := "api"

organization := "io.scalacqrs"

version := "0.3.2-SNAPSHOT"

scalaVersion := "2.11.7"

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := Some("snapshots" at sys.props.getOrElse("snapshotsRepo", default = "http://someMockRepo.com"))

libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % "2.11.7")