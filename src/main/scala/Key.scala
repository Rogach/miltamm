package org.rogach.miltamm

case class Key[A](
  calc: () => A
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

  private[miltamm] var _name: String = ""
  def name = _name
  def name(n: String): Key[A] = {
    _name = n
    this
  }
  
  def calc(fn: => A) = copy(calc = () => fn)

  def map[B](f: A => B): Key[B] = Key(() => f(apply()))
  def flatMap[B](f: A => Key[B]): Key[B] = Key(() => f(apply()).apply)
  
  // some helpers
  def when(bool: Key[Boolean], or: => A): Key[A] = bool.map { v => if (v) apply() else or }
}
