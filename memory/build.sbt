name := "scala-cqrs-memory"

version := "1.0.0"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.7",
  "org.scalatest" %% "scalatest" % "2.2.2" % Test,
  //"org.assertj" % "assertj-core" % "1.7.0" % Test, // Intellij has problem resolving assertThat, although during build it works fine
  "org.easytesting" % "fest-assert-core" % "2.0M10" % Test)