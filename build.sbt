import AssemblyKeys._

organization := "org.rogach"

name := "miltamm"

version := "0.0.2"

scalaVersion := "2.10.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked", 
  "-feature", 
  "-language:postfixOps",
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-Xlint")

scalacOptions in (Compile, doc) ++= Opts.doc.sourceUrl("https://github.com/Rogach/miltamm/tree/master/€{FILE_PATH}.scala")

// fix for paths to source files in scaladoc
doc in Compile <<= (doc in Compile) map { in =>
  Seq("bash","-c",""" for x in $(find target/scala-2.10/api/ -type f); do sed -i "s_`pwd`/__" $x; done """).!
  in
}

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