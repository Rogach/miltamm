package org.rogach.miltamm

import java.io.File

case class Action(from: Path, transform: Option[(Conf, Seq[String]) => Seq[String]], to: Option[Path]) {
  def prepend(preFrom: Path, preTo: Path) = Action(preFrom ++ from, transform, to.map(preTo ++ _))
}
