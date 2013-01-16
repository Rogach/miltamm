package org.rogach.miltamm

import org.rogach.scallop._

class Options(args: Seq[String]) extends ScallopConf(args) {
  version("Avalanche, %s b%s (%3$td.%3$tm.%3$tY %3$tH:%3$tM). Built with Scala %4$s" format (
    BuildInfo.version, 
    BuildInfo.buildinfoBuildnumber, 
    new java.util.Date(BuildInfo.buildTime),
    BuildInfo.scalaVersion))

  banner("""Template preprocessor.
           |Usage:
           |  miltamm [OPTIONS]...  TEMPLATE_DIR  DESTINATION_DIR
           |""".stripMargin)

  val buildFile = opt[String](descr = "location of template definition file. By default, miltamm uses miltamm-template.scala file right at the top of template directory")
  val template = trailArg[String]("template", descr = "location of template directory")
  val destination = trailArg[String]("destination", descr = "where to put the processed template files")
}
