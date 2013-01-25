package org.rogach.miltamm

import java.io.File

/** Description of what to do with the file.
  * The `from` prefix of file path is stripped and replaced with `to`.
  * 
  * @param from The prefix, which is to be stripped from the file path.
  * @param transform Function to apply to file contents. If `None`, file is copied as is.
  * @param to Replaces `from`. If `None`, file is ignored.
  */ 
case class Action(from: Path, transform: Option[(Conf, Seq[String]) => Seq[String]], to: Option[Path]) {
  /** Adds prefixes to `from` and `to` params of this action. 
    * Called when this action is passed up through chain of routes.
    *
    * @param preFrom prefix to prepend to `from` parameter.
    * @param preTo prefix to prepend to `to` parameter.
    */
  def prepend(preFrom: Path, preTo: Path) = Action(preFrom ++ from, transform, to.map(preTo ++ _))
}
