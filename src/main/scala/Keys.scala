package org.rogach.miltamm

trait Keys {

  def static[A](a: A) = Key(() => a)

  def ask[A](q: String, conv: String => Either[String, A]): A = {
    while (true) {
      Console.println(q)
      Console.flush()
      val in = Console.readLine()
      try {
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

  def bool(q: String): Key[Boolean] = Key(() => ask(q, boolConverter))
  
  def string(q: String): Key[String] = Key(() => ask(q, Right(_)))
}
