import AssemblyKeys._

organization := "org.rogach"

name := "miltamm"

version := "1.0.0"

scalaVersion := "2.10.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked", 
  "-feature", 
  "-language:postfixOps",
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-Xlint")

seq(Revolver.settings: _*)

seq(assemblySettings: _*)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % "2.10.0",
  "org.scala-lang" % "scala-reflect" % "2.10.0",
  "org.rogach" %% "scallop" % "0.7.0",
  "commons-io" % "commons-io" % "2.1",
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

fork in Test := true