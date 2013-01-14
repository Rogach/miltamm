package org.rogach.miltamm

import util.matching.Regex

trait Keys {

  def static[A](a: A) = Key(() => a)

  def ask[A](q: String, conv: String => Either[String, A]): A = {
    while (true) {
      Console.println(q)
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
  
  val boolConverter = (_: String).head.toLower match {
    case 'y' => Right(true)
    case 'n' => Right(false)
    case _ => Left("Failed to parse boolean value")
  }

  def check[A](rgx: Regex, conv: String => Either[String, A]): String => Either[String, A] = { x =>
    x.trim match {
      case rgx() => conv(x)
      case _ => // no match
        Left("Failed to parse, sorry. The value must match the regex: " + rgx)
    }
  }

  def bool(q: String): Key[Boolean] = Key(() => ask(q, boolConverter))
  
  def string(q: String, rgx: Regex = ".*".r): Key[String] = Key(() => ask(q, check(rgx, Right(_))))
  
  def int(q: String): Key[Int] = Key(() => ask(q, x => Right(x.trim.toInt)))
  
  def select(q: String, vals: String*): Key[String] = {
    val header = q + "\n" + vals.zipWithIndex.map { case (s, i) => "%2d - %s" format (i+1, s) }.mkString("\n")
    val parser = (v: String) => {
      val i = v.trim.toInt
      if (i > 0 && i <= vals.size) {
        Right(vals(i-1))
      } else Left("Please input number in range %d--%d" format (1, vals.size))
    }
    Key(() => ask(header, parser))
  }
}
