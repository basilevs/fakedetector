organization := "org.basilevs"

name := "fakedetector"

version := "0.0.1"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-swing" % "2.9.1",
  "org.apache.commons" % "commons-compress" % "1.3",
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

scalacOptions += "-deprecation"
