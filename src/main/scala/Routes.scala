package org.rogach.miltamm

import org.rogach.miltamm._
import java.io.File

/** Helpers to create and manipulate Route values. */
trait Routes {
  /** Creates a route, that ignores the files, starting with some path. 
    * @param p Route only matches files which names start with `p`, and returns `Ignore` action.
    */
  def ignore(p: Path) = new Route(p, None, None, Nil)

  /** Creates a route, that moves a file from one relative location to another. */
  def move(from: Path, to: Path) = new Route(from, Some(to), None, Nil)

  /** Creates a route, that preprocesses the file contents using MPP. */
  def preprocess(p: Path) = new Route(p, Some(p), Some(MPP), Nil)
  
  def movePreprocess(from: Path, to: Path) = new Route(from, Some(to), Some(MPP), Nil)

  /** Creates a route, that copies the file as-is from template to destination. */
  def copy(p: Path) = new Route(p, Some(p), None, Nil)

  /** Creates a route, that executes the child function (route) only if the provided key is true.
    * @param b key to check
    * @param route Route to delegate to, if key is true.
    */
  def iff(b: Key[Boolean])(route: PartialFunction[Path, Action]): PartialFunction[Path, Action] = {
    if (b()) route else new PartialFunction[Path, Action] {
      def isDefinedAt(f: Path) = route.isDefinedAt(f)
      def apply(f: Path) = new Action(f, None, None)
    }
  }
  
  /** Implicit converter from string to path. */
  implicit def toPath(s: String) = Path(s)

  /** Implicit converter from path to copy route. */
  implicit def pathToRoute(p: Path) = copy(p)
  
  /** Implicit converter from string to copy route. */
  implicit def stringToRoute(s: String) = copy(Path(s))

  /** Enables the ability to specify move routes like `"from" >> "to"`.
    * Replaces keys with their values in the destination part.
    */
  implicit class ToMove(from: String) {
    def >>(to: String) = {
      move(
        Path(from),
        Path(currentConf.value.keys.foldLeft(to)((path, key) => path.replace("#{" + key.name + "}", key.apply.toString)))
      )
    }
    def >>(to: Path) = move(Path(from), to)
    def >|>(to: Path) = movePreprocess(Path(from), to)
    def pp = preprocess(Path(from))
  }
}

/** Route definition class.
  * @param from Prefix for files, that will be matched.
  * @param to Where should be matched files copied. If `None`, then file is ignored.
  * @param action Transform to apply to the file. If `None`, then the file is copied as-is.
  * @param children Child routes. If one of these routes matches the provided file, then the decision is delegated to it.
  */
case class Route(from: Path, to: Option[Path], action: Option[(Conf, Seq[String]) => Seq[String]], children: List[PartialFunction[Path, Action]]) extends PartialFunction[Path, Action] {
  /** Does this route match this path? */
  def isDefinedAt(f: Path) = f.startsWith(from)

  /** Get an Action to apply to a given file. */
  def apply(f: Path) = { 
    val ff = f.drop(from.size)
    children.find(_.isDefinedAt(ff))
      .map(_.apply(ff).prepend(from, to.getOrElse(from)))
      .getOrElse(Action(from, action, to))
  }
  
  /** Append some children to this route. */
  def append(routes: Seq[PartialFunction[Path, Action]]) = copy(children = children ++ routes)
  
  /** Replace default action of this route. */
  def withAction(a: (Conf, Seq[String]) => Seq[String]) = copy(action = Some(a))

  override def toString = "Route(%s, %s, %s, %s)" format (from, to, action, children)
}

/** Helper object to split strings into paths. */
object Path {
  def apply(p: String): Path = p.split("/").filter(_.trim.nonEmpty).toSeq
}
