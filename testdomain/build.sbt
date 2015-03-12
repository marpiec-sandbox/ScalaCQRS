name := "testdomain"

organization := "io.scalacqrs"

version := "0.2.0-SNAPSHOT"

scalaVersion := "2.11.5"

publish := {}

publishLocal := {}

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.7",
  "org.scalatest" %% "scalatest" % "2.2.2" % Test,
  "pl.mpieciukiewicz.mpjsons" % "mpjsons" % "0.5.2" % Test,
  "org.apache.commons" % "commons-dbcp2" % "2.0.1" % Test,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41" % Test)