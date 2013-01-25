package org.rogach.miltamm

/** Sort of a global variable for currently used Conf.
  * It's probably not the best choice, but as for now I can't see a way 
  * to easily thread Conf through all the code.
  */
object currentConf extends util.DynamicVariable[Conf](Conf())

/** Holder for application configuration.
  * @param template The location of source template
  * @param destination The target directory for the build (where to put the resulting files).
  * @param keys All the keys that where defined during build resolution.
  */
case class Conf(
  template: String = "",
  destination: String = "",
  keys: Seq[Key[_]] = Nil
)
