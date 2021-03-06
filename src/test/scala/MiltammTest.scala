package org.rogach.miltamm

import org.scalatest.FunSuite

trait MiltammTest extends FunSuite {
  implicit class GoodEquals[A](a: A) {
    def ====[B](b: B) = assert(a === b)
  }

  def mpp(keys: Key[_]*) = new MPP(Conf("", "", keys))
}
