package org.rogach.miltamm

object Keys {
  def noParser[A] = (_:A) => sys.error("Non-implemented parser")
  def static[A](a: A) = Key("", "", () => a, noParser)
  
}

case class Key[A](
  nam: String, 
  q: String, 
  calc: () => A,
  parser: String => A
) {

  private[miltamm] var result: Option[A] = None

  def apply(): A = 
    if (result.isDefined) result.get 
    else {
      result = Some(calc())
      result.get
    }
  def get: Option[A] = result

  def isDefined: Boolean = result.isDefined

  private[miltamm] var _name: String = nam
  def name = _name
  def name(n: String): Key[A] = copy(nam = n)
  
  def question = q
  def question(qst: String): Key[A] = copy(q = qst)
  
  def calc(fn: => A) = copy(calc = () => fn)

  def map[B](f: A => B): Key[B] = Key("", "", () => f(calc()), Keys.noParser)
  def flatMap[B](f: A => Key[B]): Key[B] = Key("", "", () => f(calc()).apply, Keys.noParser)
}
