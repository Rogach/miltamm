import sbt._
import Keys._

object build extends Build {

  val testCopy = TaskKey[Unit]("test-copy") <<= (sourceDirectory, target) map { (source, target) =>
    (target / "template-output").listFiles.foreach { f =>
      if (f.isDirectory) {
        IO.delete(source / "templates-test" / (f.getName + ".result"))
        IO.copyDirectory(f, source / "templates-test" / (f.getName + ".result"))
      } else
        if (f.isFile && f.getName.endsWith(".output") && f.length > 0)
          IO.copyFile(f, source / "templates-test" / f.getName)
    }
  }

  lazy val root = Project("plugins", file("."), settings = Defaults.defaultSettings ++ Seq(testCopy))
}
