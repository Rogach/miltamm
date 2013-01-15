package org.rogach.miltamm

import java.io.File
import org.apache.commons.io.FileUtils

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
