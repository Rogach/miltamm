package org.rogach.miltamm

import BuildImports._

class MppExprTest extends MiltammTest {
  test ("single boolean true value") {
    mpp(static(true).name("a")).parseBoolean("a") ==== true
  }
  test ("single boolean false value") {
    mpp(static(false).name("a")).parseBoolean("a") ==== false
  }
  test ("int comparison") {
    mpp().parseBoolean("1 == 1")
  }
  test ("int key comparison") {
    mpp(static(1).name("a"), static(1).name("b")).parseBoolean("a == b")
  }
  test ("int key sum comparison") {
    mpp(static(1).name("a"), static(2).name("b")).parseBoolean("a + b == 3")
  }
}
