package org.rogach.miltamm

import java.lang.ProcessBuilder

object Util {
  /** Pretty logging. */
  object log {
    val INFO = "info"
    val ERROR = "error"
    val WARN = "warn"
    val SUCCESS = "success"
    def log(mess:String, level:String):Unit = {
      val prefix =
        if (System.console() == null) "[miltamm] "
        else {
         "[\033[%smmiltamm\033[0m] " format (
            level match {
              case ERROR => "31"
              case SUCCESS => "32"
              case WARN => "33"
              case _ => "0"
            }
          )
        }
      println(prefix + mess)
    }
    def info(mess:String):Unit = log(mess, INFO)
    def error(mess:String) = log(mess, ERROR)
    def warn(mess:String) = log(mess, WARN)
    def success(mess:String) = log(mess, SUCCESS)
  }

  def getTmpDir = sys.props("java.io.tmpdir") + "/miltamm-" + (0 to 8).map(_=> math.random * 10 toInt).mkString

  /** Runs the command, connects all streams (in/err/out) to those of the main process, and waits for this process to terminate. */
  def runCommand(command: String, args: String*) = {
    printf("Running: %s %s\n", command, args.mkString(" "))
    val proc = try {
      new ProcessBuilder((command +: args):_*).start()
    } catch { case e: java.io.IOException =>
      Util.log.error(s"Failed to find the command: $command")
      sys.exit(1)
    }

    def finished = try {
      proc.exitValue()
      true
    } catch { case e: java.lang.IllegalThreadStateException =>
      false
    }

    val stdout = new java.io.InputStreamReader(proc.getInputStream)
    val stderr = new java.io.InputStreamReader(proc.getErrorStream)
    val stdin = proc.getOutputStream

    // pipe stdout of the process
    new Thread(new Runnable {
      def run = {
        try {
          while (true) {
            Thread.sleep(25)
            while (stdout.ready()) {
              val read = stdout.read()
              if (read != -1) {
                java.lang.System.out.print(read.toChar)
                java.lang.System.out.flush()
              }
            }
          }
        } catch { case e: java.io.IOException => }
      }
    }).start()

    // pipe stderr of the process
    new Thread(new Runnable {
      def run = {
        try {
          while (true) {
            Thread.sleep(25)
            while (stderr.ready()) {
              val read = stderr.read()
              if (read != -1) {
                java.lang.System.err.print(read.toChar)
                java.lang.System.err.flush()
              }
            }
          }
        } catch { case e: java.io.IOException => }
      }
    }).start()

    // pipe stdin to the process
    while (!finished) {
      Thread.sleep(25)
      val available = java.lang.System.in.available
      if (available > 0) {
        val arr = new Array[Byte](available)
        java.lang.System.in.read(arr)
        stdin.write(arr)
      }
    }

    Thread.sleep(100) // wait for stderr and stdout
    stdout.close()
    stderr.close()
    stdin.close()
  }

}
