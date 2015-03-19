name := "api"

organization := "io.scalacqrs"

version := "0.2.1-SNAPSHOT"

scalaVersion := "2.11.5"

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := Some("snapshots" at sys.props.getOrElse("mavenRepo", default = "http://someMockRepo.com"))