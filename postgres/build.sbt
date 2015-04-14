name := "postgres"

organization := "io.scalacqrs"

version := "0.2.3-SNAPSHOT"

scalaVersion := "2.11.5"

resolvers ++= Seq("marpiec BinTray" at "http://dl.bintray.com/marpiec/maven/")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := Some("snapshots" at sys.props.getOrElse("mavenRepo", default = "http://someMockRepo.com"))

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.7",
  "pl.mpieciukiewicz.mpjsons" % "mpjsons" % "0.5.2" % Test,
  "org.scalatest" %% "scalatest" % "2.2.2" % Test,
  "org.apache.commons" % "commons-dbcp2" % "2.0.1" % Test,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41" % Test)