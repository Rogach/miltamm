package org.rogach.miltamm

import org.rogach.miltamm._
import java.io.File

trait Routes {
  def ignore(p: Path) = new Route(p, None, None, Nil)
  def move(from: Path, to: Path) = new Route(from, Some(to), None, Nil)
  def copy(p: Path) = new Route(p, Some(p), None, Nil)
  
  implicit def toPath(s: String) = Path(s)
  implicit def pathToRoute(p: Path) = copy(p)
  implicit def stringToRoute(s: String) = copy(Path(s))
  implicit class ToMove(from: String) {
    def >>(to: String) = move(Path(from), Path(to))
    def >>(to: Path) = move(Path(from), to)
  }
}

case class Route(from: Path, to: Option[Path], action: Option[Seq[String] => Seq[String]], children: List[PartialFunction[Path, Action]]) extends PartialFunction[Path, Action] {
  def isDefinedAt(f: Path) = f.startsWith(from)
  def apply(f: Path) = { 
    val ff = f.drop(from.size)
    children.find(_.isDefinedAt(ff))
      .map(_.apply(ff).prepend(from, to.getOrElse(from)))
      .getOrElse(Action(from, action, to))
  }
  def append(routes: Seq[PartialFunction[Path, Action]]) = copy(children = children ++ routes)
  def withAction(a: Seq[String] => Seq[String]) = copy(action = Some(a))
}

object Path {
  def apply(p: String): Path = p.split("/").filter(_.trim.nonEmpty).toSeq
}
