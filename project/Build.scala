import sbt._
import Keys._
import org.apache.commons.io.FileUtils

object build extends Build {

  val testCopy = TaskKey[Unit]("test-copy") <<= (test, sourceDirectory, target) map { (_, source, target) =>
    (target / "template-output").listFiles.foreach { f =>
      if (f.isDirectory) {
        FileUtils.deleteDirectory(source / "templates-test" / (f.getName + ".result"))
        FileUtils.copyDirectory(f, source / "templates-test" / (f.getName + ".result"))
      } else 
        if (f.isFile && f.getName.endsWith(".output") && f.length > 0) 
          FileUtils.copyFile(f, source / "templates-test" / f.getName)
    }
  }

  lazy val root = Project("plugins", file("."), settings = Defaults.defaultSettings ++ Seq(testCopy))
}
