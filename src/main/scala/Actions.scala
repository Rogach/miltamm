package org.rogach.miltamm

import java.io.File
import org.apache.commons.io.FileUtils

case class Action(from: Path, transform: Option[Seq[String] => Seq[String]], to: Option[Path]) {
  def prepend(preFrom: Path, preTo: Path) = Action(preFrom ++ from, transform, to.map(preTo ++ _))
}
