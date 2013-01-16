package org.rogach.miltamm

trait BuildTemplate {

  def routes: Seq[PartialFunction[Seq[String], Action]] = Nil

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
