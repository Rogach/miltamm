package org.rogach.miltamm

import scala.reflect.runtime.universe._

/** Encapsulates the value to be computed.
  * When the value is computed, caches it.
  * @param calc function to call when calculating value
  * @param tp TypeTag for the key type
  */
case class Key[A](
  calc: () => A,
  tp: TypeTag[A]
) {

  private[miltamm] var result: Option[A] = None

  /** Get this key's value. If possible, value is retrieved from cache.
    */
  def apply(): A =
    if (result.isDefined) result.get
    else {
      result = Some(calc())
      result.get
    }
  /** Get this key value from cache. If it wasn't computed yet, returns `None`. */
  def get: Option[A] = result

  /** Was this value already computed? */
  def isDefined: Boolean = result.isDefined

  private[miltamm] var _name: String = ""
  /** Name of the key */
  def name = _name
  /** Sets the name of this key. Returns the key itself. */
  def name(n: String): Key[A] = {
    _name = n
    this
  }

  /** Creates a new key, with other computation inside. */
  def calc(fn: => A) = copy(calc = () => fn)

  /** Maps the value inside the key. */
  def map[B](f: A => B)(implicit tt: TypeTag[B]): Key[B] = Key(() => f(apply()), tt)
  /** Flat-maps the value insid the key. This funciton enables use of Key in for-comprehensions. */
  def flatMap[B](f: A => Key[B])(implicit tt: TypeTag[B]): Key[B] = Key(() => f(apply()).apply, tt)

  /** Sets this key to be computed only if the provided key is true.
    * @param bool If is true, then this value is unchanged. If false, default value is returned.
    * @param or Value to use if boolean key was false.
    */
  def when(bool: Key[Boolean], or: => A): Key[A] = bool.map { v => if (v) apply() else or }(tp)

  /** Sets this key to be computed only if the provided key is false.
    * @param bool If is false, then this value is unchanged. If true, default value is returned.
    * @param or Value to use if boolean key was true.
    */
  def unless(bool: Key[Boolean], or: => A): Key[A] = when(bool.map(!_), or)
}
