package org.rogach.miltamm

import scala.util.parsing.combinator._
import scala.util.parsing.input._

/** Miltamm PreProcessor */
object MPP extends ((Conf, Seq[String]) => Seq[String]) {
  def apply(conf: Conf, lines: Seq[String]) = {
    new MPP(conf).process(lines)
  }
}

class MPP(conf: Conf) extends Parsers {
  type Elem = String

  def process(lines: Seq[String]): Seq[String] = {
    block(new ListReader(lines.toList)) match {
      case Success(res, _) => res
      case Failure(msg, rest) =>
        Util.log.error(s"preprocessor parse failure: '$msg' on '${rest.first}'")
        sys.exit(1)
      case Error(msg, rest) =>
        Util.log.error(s"preprocessor parse error: '$msg' on '${rest.first}'")
        sys.exit(1)
    }
  }
  case class ListReader(l: List[Elem]) extends Reader[Elem] {
    def atEnd = l.isEmpty
    def first = l.head
    def pos = NoPosition
    def rest = if (atEnd) this else ListReader(l.tail)
  }

  def hash(name: String): Parser[String] = Parser { in =>
    if (in.atEnd) Failure("end of input", in.rest)
    else if (in.first.trim.startsWith("#"+name))
           Success(in.first.trim.stripPrefix("#"+name), in.rest)
         else
           Failure(s"can't match '#$name' on '${in.first}'", in)
  }
  def plain: Parser[String] = Parser { in =>
    if (in.atEnd) {
      Failure("end of file", in)
    } else {
      lazy val s = in.first.trim
      if (s.startsWith("#if") ||
          s.startsWith("#elif") ||
          s.startsWith("#else") ||
          s.startsWith("#fi")) {
        Failure(s"this is not a plain line, contains reserved identifier: '${in.first}'", in)
      } else {
        Success(
          conf.keys.foldLeft(in.first)(
            (line, key) => line.replace("#{" + key.name + "}", key.apply.toString)
          ),
          in.rest
        )
      }
    }
  }
  
  def block: Parser[Seq[String]] = If | plain.*

  def If: Parser[Seq[String]] = 
    hash("if") ~ block ~ (hash("elif") ~ block).* ~ (hash("else") ~ block).? ~ hash("fi") ^^
    { case exprIf ~ trueBlock ~ elifs ~ elseBlock ~ fi =>
      if (parseBoolean(exprIf)) 
        trueBlock
      else
        elifs.find(elif => parseBoolean(elif._1)).orElse(elseBlock).map(_._2).getOrElse(Nil)
    }
    
  def parseBoolean(expr: String): Boolean = {
    import scala.reflect.runtime._;
    import scala.tools.reflect.ToolBox;
    val toolbox = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()
    import scala.reflect.runtime.universe._
    
    val booleanTag = implicitly[TypeTag[Boolean]]
    val stringTag = implicitly[TypeTag[String]]
    val intTag = implicitly[TypeTag[Int]]
    val doubleTag = implicitly[TypeTag[Double]]
    
    val vals: Seq[String] = conf.keys.collect {
      case key if key.tp.tpe =:= booleanTag.tpe || key.tp.tpe =:= intTag.tpe || key.tp.tpe =:= doubleTag.tpe =>
        s"val ${key.name} = ${key.apply}"
      case key if key.tp.tpe =:= stringTag.tpe =>
        s""" val ${key.name} = "${key.apply}"; """
    }
    
    toolbox.eval(toolbox.parse(vals.mkString + expr)) match {
      case b: Boolean => b
      case other => 
        Util.log.error(s"Expected boolean, found '${other.getClass}': $expr")
        sys.exit(1)
    }
  }
  
}
