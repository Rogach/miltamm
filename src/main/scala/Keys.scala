package org.rogach.miltamm

import util.matching.Regex
import scala.reflect.runtime.universe._

/** Helpers to create and manipulate Key values. */
trait Keys {

  /** Create a key with a pre-defined value.
    * @param value Predefined value
    */
  def static[A](value: A)(implicit tt: TypeTag[A]) = Key(() => value, tt)

  /** Ask user a question.
    * @param q Question text
    * @param default Value to be used, if user entered empty string
    * @param conv Converter for inputted value. In case of `Left`, string is displayed to the user and he is asked to repeat the input.
    */
  def ask[A](q: String, default: String, conv: String => Either[String, A]): A = {
    while (true) {
      Console.println(q)
      Console.printf("[%s]: ", default)
      Console.flush()
      val in = Console.readLine()
      if (in == null) {
        Util.log.error("End of input, aborting compilation")
        sys.exit(1)
      }
      try {
        conv(if (in.trim.nonEmpty) in else default).fold(println, return _)
        () // stop the compiler from warning me about something
      } catch { case e: Exception =>
        printf("Error while parsing: %s - %s\n", e.getClass, e.getMessage)
      }
    }
    sys.error("Won't happen")
  }
  
  /** Converts Y/N string into boolean value. */
  val boolConverter = (_: String).head.toLower match {
    case 'y' | 't' => Right(true)
    case 'n' | 'f' => Right(false)
    case _ => Left("Failed to parse boolean value")
  }

  /** Create a "checked" converter. Before feeding the string into converter, it is matched against a given regex. In case of error, the message is generated, containing the regex as explanation..
    * @param rgx regex to check with
    * @param conv converter to wrap
    */
  def check[A](rgx: Regex, conv: String => Either[String, A]): String => Either[String, A] = { x =>
    x.trim match {
      case rgx() => conv(x)
      case _ => // no match
        Left("Failed to parse, sorry. The value must match the regex: " + rgx)
    }
  }

  /** Create a boolean key, that would ask the user for a value when computed.
    * @param q question string
    * @param default Value to use if user entered empty string
    */
  def bool(q: String, default: Boolean): Key[Boolean] = 
    Key(() => ask(q, if (default) "yes" else "false", boolConverter), implicitly[TypeTag[Boolean]])
  
  /** Create a string key, that would ask the user for a value when computed.
    * @param q question string
    * @param default Value to use if user entered empty string
    */
  def string(q: String, default: String, rgx: Regex = ".*".r): Key[String] = 
    Key(() => ask(q, default, check(rgx, Right(_))), implicitly[TypeTag[String]])
  
  /** Create an integer key, that would ask the user for a number when computed.
    * @param q question string
    * @param default Value to use if user entered empty string
    */
  def int(q: String, default: Int): Key[Int] = 
    Key(() => ask(q, default.toString, x => Right(x.trim.toInt)), implicitly[TypeTag[Int]])
  
  /** Create an string key, that would ask the user to choose from a number of options when computing.
    * @param q question string
    * @param vals List of tuples - (key, description). Description is shown to the user, key is returned for internal use.
    */
  def select(q: String, vals: (String, String)*): Key[String] = {
    val header = q + "\n" + vals.zipWithIndex.map { case (s, i) => "%2d - %s" format (i+1, s._2) }.mkString("\n")
    val parser = (v: String) => {
      val i = v.trim.toInt
      if (i > 0 && i <= vals.size) {
        Right(vals(i-1)._1)
      } else Left("Please input number in range %d--%d" format (1, vals.size))
    }
    Key(() => ask(header, "1", parser), implicitly[TypeTag[String]])
  }
}
