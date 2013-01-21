import sbt._
import Keys._
import org.apache.commons.io.FileUtils
import sbtassembly.Plugin.AssemblyKeys._
import proguard.{Configuration => ProGuardConfiguration, ProGuard, ConfigurationParser};

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
  
  val proguardMain = "org.rogach.miltamm.Main"
  val proguardConfig = """
-injars %s
-outjars %s
-libraryjars %s
-keep public class %s { static void main(java.lang.String[]); }

-dontwarn scala.**
-dontnote scala.**
-dontwarn ch.epfl.**
-keep class * implements org.xml.sax.EntityResolver
-keepclassmembers class * {
    ** MODULE$;
}
-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
    long eventCount;
    int  workerCounts;
    int  runControl;
    scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode syncStack;
    scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode spareStack;
}
-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinWorkerThread {
    int base;
    int sp;
    int runState;
}
-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinTask {
    int status;
}
-keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue {
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference head;
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference tail;
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference cleanMe;
}
"""
  val proguard = TaskKey[Unit]("proguard") <<= 
    (assembly, jarName in assembly, baseDirectory, target) map { (_, assemblyJar, base, target) =>
      val config = new ProGuardConfiguration
      val args = proguardConfig format (
        target / assemblyJar getAbsolutePath,
        target / ("proguard-" + assemblyJar) getAbsolutePath,
        file(sys.Prop[String]("java.home").get) / "lib" / "rt.jar" getAbsolutePath,
        proguardMain
      )
      new ConfigurationParser(args, "", base, new java.util.Properties).parse(config)
      new ProGuard(config).execute
    }

  lazy val root = Project("plugins", file("."), settings = Defaults.defaultSettings ++ Seq(testCopy, proguard))
}
