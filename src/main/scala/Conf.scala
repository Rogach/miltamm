package org.rogach.miltamm

/** Sort of a global variable for currently used Conf.
  * It's probably not the best choice, but as for now I can't see a way 
  * to easily thread Conf through all the code.
  */
object currentConf extends util.DynamicVariable[Conf](Conf())

case class Conf(
  template: String = "",
  destination: String = "",
  keys: Seq[Key[_]] = Nil
)
