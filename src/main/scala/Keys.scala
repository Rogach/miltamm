package org.rogach.miltamm

import util.matching.Regex

/** Helpers to create and manipulate Key values. */
trait Keys {

  /** Create a key with a pre-defined value.
    * @param value Predefined value
    */
  def static[A](value: A) = Key(() => value)

  /** Ask user a question.
    * @param q Question text
    * @param prompt Prompt to be displayed before the answer input.
    * @param conv Converter for inputted value. In case of `Left`, string is displayed to the user and he is asked to repeat the input.
    */
  def ask[A](q: String, prompt: String = "", conv: String => Either[String, A]): A = {
    while (true) {
      Console.println(q)
      Console.print(prompt)
      Console.flush()
      val in = Console.readLine()
      if (in == null) {
        Util.log.error("End of input, aborting compilation")
        sys.exit(1)
      }
      try {
        if (in.trim.nonEmpty)
          conv(in).fold(println, return _)
        () // stop compiler from warning me about something
      } catch { case e: Exception =>
        printf("Error while parsing: %s - %s\n", e.getClass, e.getMessage)
      }
    }
    sys.error("Won't happen")
  }
  
  /** Converts Y/N string into boolean value. */
  val boolConverter = (_: String).head.toLower match {
    case 'y' => Right(true)
    case 'n' => Right(false)
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
    */
  def bool(q: String, prompt: String = "[y/n]: "): Key[Boolean] = Key(() => ask(q, prompt, boolConverter))
  
  /** Create a string key, that would ask the user for a value when computed.
    * @param q question string
    */
  def string(q: String, prompt: String = "> ", rgx: Regex = ".*".r): Key[String] = Key(() => ask(q, prompt, check(rgx, Right(_))))
  
  /** Create an integer key, that would ask the user for a number when computed.
    * @param q question string
    */
  def int(q: String, prompt: String = "(integer value): "): Key[Int] = Key(() => ask(q, prompt, x => Right(x.trim.toInt)))
  
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
    Key(() => ask(header, "(1-%d): " format vals.size, parser))
  }
}
