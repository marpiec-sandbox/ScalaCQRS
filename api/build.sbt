name := "api"

organization := "pl.mpieciukiewicz.scala-cqrs"

version := "0.1.2-SNAPSHOT"

scalaVersion := "2.11.5"

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := Some("snapshots" at sys.props.getOrElse("mavenRepo", default = ""))