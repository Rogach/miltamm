package org.rogach.miltamm

/** Wrapper template for build files.
  */
trait BuildTemplate {

  /** Transform, that is applied to files in the template.
    */
  def routes: Seq[PartialFunction[Seq[String], Action]] = Nil

  /** Resolves all the keys in this build template: fills in missing names,
    * computes values and caches them.
    */
  private[miltamm] def resolveKeys(): Seq[Key[_]] = {
    val keys = this.getClass.getMethods
        .filterNot(classOf[BuildTemplate].getMethods.toSet)
        .filterNot(_.getName.endsWith("$eq"))
        .filterNot(_.getName.endsWith("$outer"))
        .filter(_.getReturnType == classOf[Key[_]])
        .filter(_.getParameterTypes.isEmpty)
    keys.map { m =>
      val key = m.invoke(this).asInstanceOf[Key[_]]
      key._name = m.getName
      key.apply
      key
    }
  }

}
