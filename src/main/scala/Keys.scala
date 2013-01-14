package org.rogach.miltamm

trait Keys {
  def noParser[A] = (_:A) => sys.error("Non-implemented parser")

  def static[A](a: A) = Key("", "", () => a, noParser)

  def ask[A](q: String, conv: String => A): A = {
    while (true) {
      Console.println(q)
      Console.flush()
      val in = Console.readLine()
      try {
        return conv(in)
      } catch { case e: Throwable =>
        printf("Error while parsing: %s - %s\n", e.getClass, e.getMessage)
      }
    }
    sys.error("Won't happen")
  }
}
