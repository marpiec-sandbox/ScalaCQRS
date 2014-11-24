name := "scala-cqrs-jdbc"

version := "1.0.0"

scalaVersion := "2.11.4"


resolvers ++= Seq("marpiec BinTray" at "http://dl.bintray.com/marpiec/maven/")

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.7",
  "org.springframework" % "spring-jdbc" % "4.0.5.RELEASE",
  "org.springframework" % "spring-transactions" % "4.0.5.RELEASE",
  "pl.mpieciukiewicz.mpjsons" % "mpjsons" % "0.5.1",
  "org.scalatest" %% "scalatest" % "2.2.2" % Test,
  //"org.assertj" % "assertj-core" % "1.7.0" % Test, // Intellij has problem resolving assertThat, although during build it works fine
  "org.easytesting" % "fest-assert-core" % "2.0M10" % Test)