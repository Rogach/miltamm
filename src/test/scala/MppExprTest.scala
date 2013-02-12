package org.rogach.miltamm

import BuildImports._

class MppExprTest extends MiltammTest {
  test ("single boolean true value") {
    val mpp = new MPP(Conf("", "", Seq(static(true).name("a"))))
    mpp.parseBoolean("a") ==== true
  }
  test ("single boolean false value") {
    val mpp = new MPP(Conf("", "", Seq(static(false).name("a"))))
    mpp.parseBoolean("a") ==== false
  }
  test ("int comparison") {
    val mpp = new MPP(Conf("", "", Seq()))
    mpp.parseBoolean("1 == 1")
  }
  test ("int key comparison") {
    val mpp = new MPP(Conf("", "", Seq(static(1).name("a"), static(1).name("b"))))
    mpp.parseBoolean("a == b")
  }
  test ("int key sum comparison") {
    val mpp = new MPP(Conf("", "", Seq(static(1).name("a"), static(2).name("b"))))
    mpp.parseBoolean("a + b == 3")
  }
}
