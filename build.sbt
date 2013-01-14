import AssemblyKeys._

organization := "org.rogach"

name := "miltamm"

version := "0.1.0"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint")

seq(Revolver.settings: _*)

seq(assemblySettings: _*)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % "2.10.0",
  "org.scala-lang" % "scala-reflect" % "2.10.0",
  "org.rogach" %% "scallop" % "0.7.0",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

mainClass in assembly := Some("org.rogach.miltamm.Main")

jarName in assembly <<= (version, name) { (v,n) =>  "%s-%s.jar" format (n, v) }

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](
  name, 
  version,
  scalaVersion, 
  sbtVersion,
  buildInfoBuildNumber,
  "buildTime" -> {() => System.currentTimeMillis}
)

buildInfoPackage := "org.rogach.miltamm"

site.settings

site.includeScaladoc()

ghpages.settings

git.remoteRepo := "git@github.com:Rogach/miltamm.git"