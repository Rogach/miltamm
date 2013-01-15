package org.rogach.miltamm

import java.io.File
import org.apache.commons.io.FileUtils

trait Routes {
  
}

class Route(prefix: Path, action: Action, children: List[Route]) extends PartialFunction[String, Unit] {
  def isDefinedAt(f: String) = Path(f).startsWith(prefix)
  def apply(f: String) = children.find(_.isDefinedAt(f)).map(_.prepend(prefix).apply(f)).getOrElse(action(prefix, f))
  def prepend(before: Path) = new Route(prefix.prepend(before), action, children)
  def append(routes: Seq[Route]) = new Route(prefix, action, children ++ routes)
}

object Path {
  def apply(p: String): Path = Path(p.split("/").filter(_.trim.nonEmpty).toSeq)
}
case class Path(p: Seq[String]) {
  def prepend(before: Path) = Path(before.p ++ p)
  def append(after: Path) = Path(p ++ after.p)
  def file = new File(toString)
  def startsWith(other: Path) = p.startsWith(other.p)
  override def toString = p.mkString("/")
}

trait Action {
  def apply(prefix: Path, fullName: String)
}
object Actions {
  object Ignore extends Action {
    def apply(prefix: Path, fullName: String) = {}
  }
  object Copy extends Action {
    def apply(prefix: Path, fullName: String) = {
      FileUtils.copyFile(new File(Main.opts.template() + "/" + fullName), new File(Main.opts.destination() + "/" + fullName))
    }
  }
  class Move(from: Path, to: Path) extends Action {
    def apply(prefix: Path, fullName: String) = {
      val dest = Path(prefix.p.dropRight(from.p.size) ++ to.p)
      FileUtils.copyFile(new File(Main.opts.template() + "/" + fullName), new File(Main.opts.destination() + "/" + dest))
    }
  }
}
