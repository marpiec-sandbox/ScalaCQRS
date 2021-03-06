name := "testdomain"

organization := "io.scalacqrs"

version := "0.3.2-SNAPSHOT"

scalaVersion := "2.11.7"

publish := {}

publishLocal := {}

resolvers in ThisBuild ++= Seq(
  "eclipse repo" at "https://repo.eclipse.org/content/groups/releases/",
  "Sonatype repo" at "https://oss.sonatype.org/content/repositories/releases/",
  "JT Weston Snapshots" at "http://vps120320.ovh.net:9081/nexus/content/repositories/jtweston-snapshots/",
  "marpiec BinTray" at "http://dl.bintray.com/marpiec/maven/",
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository")

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.7",
  "org.scala-lang" % "scala-reflect" % "2.11.7",
  "org.scalatest" %% "scalatest" % "2.2.2" % Test,
  "io.mpjsons" %% "mpjsons" % "0.6.12" % Test,
  "org.apache.commons" % "commons-dbcp2" % "2.0.1" % Test,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41" % Test)