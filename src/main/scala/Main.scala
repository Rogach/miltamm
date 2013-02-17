package org.rogach.miltamm

import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import collection.JavaConversions._

object Main extends App {
  run(args)

  object buildOptions {
    var allDefault = false
  }

  def run(args: Seq[String]) = {
    val opts = new Options(args)
    buildOptions.allDefault = opts.allDefault()
    val template = prepareTemplate(opts)
    val build = BuildCompiler.compileFile(opts.buildFile.get.getOrElse(template + "/miltamm-template.scala"), opts)
    val conf = Conf(template, opts.destination(), build.resolveKeys())
    val templateDir = Path(new File(conf.template).getAbsolutePath)
    val outputDir = Path(new File(conf.destination).getAbsolutePath)

    FileUtils.deleteDirectory(new File(conf.destination))

    currentConf.withValue(conf) {
      val route = BuildImports.preprocess(Nil)
        .append(if (opts.buildFile.get.nonEmpty) Nil else List(BuildImports.ignore(Seq("miltamm-template.scala")))) // exclude template file
        .append(build.routes)
      FileUtils.iterateFiles(new File(conf.template), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).foreach { f =>
        val relPath = Path(f.getAbsolutePath).drop(templateDir.size)
        val action = route(relPath)
        action.to.map(dest => new File(outputDir ++ dest ++ relPath.drop(action.from.size) map ("/"+) mkString)).foreach { dest =>
          println(s"File: $dest")
          action.transform.fold { 
            // short-circuit, since we don't need to transform contents of the file
            FileUtils.copyFile(f, dest)
          } { trans =>
            FileUtils.writeLines(dest, trans(conf, FileUtils.readLines(f)))
          }
        }
      }
    }
  }

  /** Prepares the template for processing. (by downloading it, if required) 
    * @return the path to the directory on filesystem, that contains the prepared template
    */
  def prepareTemplate(opts: Options): String = {
    if (opts.git()) {
      val tmpDir = Util.getTmpDir
      Util.runCommand("git", "clone", "--progress", opts.template(), tmpDir)
      tmpDir + "/" + opts.gitSubpath()
    } else if (opts.rsync()) {
      val tmpDir = Util.getTmpDir
      Util.runCommand("rsync", "-avrz", opts.template(), tmpDir)
      tmpDir
    } else {
      // everything is already in place
      opts.template()
    }
  }

}
