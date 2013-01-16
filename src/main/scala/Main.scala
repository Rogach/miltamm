package org.rogach.miltamm

import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import collection.JavaConversions._

object Main extends App {
  run(args)

  def run(args: Seq[String]) = {
    val opts = new Options(args)
    val build = BuildCompiler.compile(opts.buildFile.get.getOrElse(opts.template() + "/miltamm-template.scala"))
    val conf = Conf(opts.template(), opts.destination(), build.resolveKeys())
    val route = BuildImports.copy(Nil) append build.transform

    val templateDir = Path(new File(conf.template).getAbsolutePath)
    FileUtils.deleteDirectory(new File(conf.template))
    FileUtils.iterateFiles(new File(conf.template), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).foreach { f =>
      val relPath = Path(f.getAbsolutePath).drop(templateDir.size)
      val action = route(relPath)
      action.to.map(dest => new File(templateDir ++ dest ++ relPath.drop(action.from.size) mkString "/")).foreach { dest =>
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
