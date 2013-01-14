package org.rogach.miltamm

object Util {
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
    def log(mess:String):Unit = log(mess, INFO)
    def error(mess:String) = log(mess, ERROR)
    def warn(mess:String) = log(mess, WARN)
    def success(mess:String) = log(mess, SUCCESS)
  }

}
