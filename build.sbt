import AssemblyKeys._

organization := "org.rogach"

name := "miltamm"

version := "0.0.1"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint")

seq(Revolver.settings: _*)

seq(assemblySettings: _*)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % "2.10.0",
  "org.scala-lang" % "scala-reflect" % "2.10.0",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

mainClass in assembly := Some("org.rogach.miltamm.Main")
