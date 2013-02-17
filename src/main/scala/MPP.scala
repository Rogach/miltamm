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
    phrase(block)(new ListReader(lines.toList)) match {
      case Success(res, _) => res
      case Failure(msg, rest) =>
        Util.log.error(s"preprocessor parse failure: '$msg' on '${rest.first}'")
        sys.exit(1)
      case Error(msg, rest) =>
        Util.log.error(s"preprocessor parse error: '$msg' on '${rest.first}'")
        sys.exit(1)
    }
  }
  case class ListReader(l: List[Elem], line: Int = 1) extends Reader[Elem] {
    def atEnd = l.isEmpty
    def first = l.head
    def pos = ListPosition(line, if (atEnd) "" else l.head)
    def rest = if (atEnd) this else ListReader(l.tail, line + 1)
  }
  case class ListPosition(line: Int, lineContents: String) extends Position {
    val column = 0
    override def longString: String = toString
    override def toString = s"$line: $lineContents"
  }

  /** Case class for the preprocessor instruction (like '#if'). Contains the position in the input and the expression after instruction. */
  case class Hash(pos: Position, expr: String)
  def hash(name: String): Parser[Hash] = Parser { in =>
    if (in.atEnd) Failure("end of input", in.rest)
    else if (in.first.trim.startsWith("#"+name))
           Success(Hash(in.pos, in.first.trim.stripPrefix("#"+name).trim), in.rest)
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
  
  def block: Parser[Seq[String]] = 
    rep(If | (plain.* filter (_.size > 0))) map (_.flatten)

  def If: Parser[Seq[String]] = 
    hash("if") ~ block ~ (hash("elif") ~ block).* ~ (hash("else") ~ block).? ~ hash("fi") ^^
    { case Hash(posIf, exprIf) ~ trueBlock ~ elifs ~ elseBlock ~ fi =>
      if (parseBoolean(exprIf, posIf))
        trueBlock
      else
        elifs.find(elif => parseBoolean(elif._1.expr, elif._1.pos)).orElse(elseBlock).map(_._2).getOrElse(Nil)
    }
    
  def parseBoolean(expr: String, pos: Position = NoPosition): Boolean = {
    import scala.reflect.runtime.universe._;
    val bool = implicitly[TypeTag[Boolean]]

    conf.keys.find(k => k.name == expr && k.tp.tpe =:= bool.tpe).map { key =>
      key().asInstanceOf[Boolean]
    }.orElse {
      val andRgx = """(.*)&&(.*)""".r
      val orRgx = """(.*)\|\|(.*)""".r
      expr match {
        case andRgx(a,b) => 
          (conf.keys.find(_.name == a.trim), conf.keys.find(_.name == b.trim)) match {
            case (Some(ak), Some(bk)) if ak.tp.tpe =:= bool.tpe && bk.tp.tpe =:= bool.tpe  => Some(ak().asInstanceOf[Boolean] && bk().asInstanceOf[Boolean])
            case _ => None
          }
        case orRgx(a,b) =>
          (conf.keys.find(_.name == a.trim), conf.keys.find(_.name == b.trim)) match {
            case (Some(ak), Some(bk)) if ak.tp.tpe =:= bool.tpe && bk.tp.tpe =:= bool.tpe  => Some(ak().asInstanceOf[Boolean] || bk().asInstanceOf[Boolean])
            case _ => None
          }
        case _ => None
      }
    }.getOrElse {
      import scala.reflect.runtime._;
      import scala.tools.reflect.ToolBox;
      val toolbox = universe.runtimeMirror(this.getClass.getClassLoader).mkToolBox()
      import scala.reflect.runtime.universe._;
      
      val booleanTag = implicitly[TypeTag[Boolean]]
      val stringTag = implicitly[TypeTag[String]]
      val intTag = implicitly[TypeTag[Int]]
      val doubleTag = implicitly[TypeTag[Double]]
      
      val vals: Seq[String] = conf.keys.collect {
        case key if key.tp.tpe =:= booleanTag.tpe || key.tp.tpe =:= intTag.tpe || key.tp.tpe =:= doubleTag.tpe =>
          s"val ${key.name} = ${key.apply};"
        case key if key.tp.tpe =:= stringTag.tpe =>
          s""" val ${key.name} = "${key.apply}"; """
      }
      
      try {
        toolbox.eval(toolbox.parse(vals.mkString + expr)) match {
          case b: Boolean => b
          case other =>
            Util.log.error(s"Expected boolean, found '${other.getClass}':\n${pos.longString}")
            sys.exit(1)
        }
      } catch { case e: Throwable =>
        val msg = e.getMessage.stripPrefix("reflective compilation has failed: \n\n")
        Util.log.error(s"Error while compiling boolean expression:$msg\n${pos.longString}")
        sys.exit(1)
      }
    }
  }
  
}
