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
  val silent = opt[Boolean](descr = "supress info output")
  val template = trailArg[String]("template", descr = "location of template directory")
  val destination = trailArg[String]("destination", descr = "where to put the processed template files")
  
  val allDefault = opt[Boolean](descr = "Do not ask user anything. Use default values for all keys.", default = Some(false))
  
  val git = opt[Boolean](descr = "treat the template dir as a git repository. If the template is not located at the root of repository, use '--git-subpath' option. This option is only usable if you have git installed on your system.")
  val gitSubpath = opt[String](descr = "subpath in git repo, that should contain the template.", default = Some(""))
  val rsync = opt[Boolean](descr = "treat the template dir as a remote location, downloadable via rsync. This option is only usable if you have rsync installed on your system.")
  mutuallyExclusive(git, rsync)
}
