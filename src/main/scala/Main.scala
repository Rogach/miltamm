package org.rogach.miltamm

import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import collection.JavaConversions._

object Main extends App {
  run(args)

  def run(args: Seq[String]) = {
    val opts = new Options(args)
    val build = BuildCompiler.compileFile(opts.buildFile.get.getOrElse(opts.template() + "/miltamm-template.scala"), opts)
    val conf = Conf(opts.template(), opts.destination(), build.resolveKeys())
    val templateDir = Path(new File(conf.template).getAbsolutePath)
    val outputDir = Path(new File(conf.destination).getAbsolutePath)

    FileUtils.deleteDirectory(new File(conf.destination))

    currentConf.withValue(conf) {
      val route = BuildImports.copy(Nil)
        .append(if (opts.buildFile.get.nonEmpty) Nil else List(BuildImports.ignore(Seq("miltamm-template.scala")))) // exclude template file
        .append(build.routes)
      FileUtils.iterateFiles(new File(conf.template), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).foreach { f =>
        val relPath = Path(f.getAbsolutePath).drop(templateDir.size)
        val action = route(relPath)
        action.to.map(dest => new File(outputDir ++ dest ++ relPath.drop(action.from.size) map ("/"+) mkString)).foreach { dest =>
          println(s"File: $f")
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

}
