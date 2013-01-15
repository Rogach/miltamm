package org.rogach.miltamm

trait BuildTemplate {

  def transform: Seq[PartialFunction[String, Unit]]

  private[miltamm] def resolveKeys() {
    val keys = this.getClass.getMethods
        .filterNot(classOf[BuildTemplate].getMethods.toSet)
        .filterNot(_.getName.endsWith("$eq"))
        .filterNot(_.getName.endsWith("$outer"))
        .filter(_.getReturnType == classOf[Key[_]])
        .filter(_.getParameterTypes.isEmpty)
    keys.foreach { m =>
      val key = m.invoke(this).asInstanceOf[Key[_]]
      key.apply
      key._name = m.getName
    }
  }

}
