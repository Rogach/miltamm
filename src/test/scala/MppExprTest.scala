package org.rogach.miltamm

import BuildImports._

class MppExprTest extends MiltammTest {
  test ("single boolean true value") {
    mpp(static(true).name("a")).parseBoolean("a") ==== true
  }
  test ("single boolean false value") {
    mpp(static(false).name("a")).parseBoolean("a") ==== false
  }
  test ("booleans - or") {
    mpp(static(true).name("a"), static(true).name("b")).parseBoolean("a || b") ==== true
    mpp(static(true).name("a"), static(false).name("b")).parseBoolean("a || b") ==== true
    mpp(static(false).name("a"), static(true).name("b")).parseBoolean("a || b") ==== true
    mpp(static(false).name("a"), static(false).name("b")).parseBoolean("a || b") ==== false
  }
  test ("booleans - and") {
    mpp(static(true).name("a"), static(true).name("b")).parseBoolean("a && b") ==== true
    mpp(static(true).name("a"), static(false).name("b")).parseBoolean("a && b") ==== false
    mpp(static(false).name("a"), static(true).name("b")).parseBoolean("a && b") ==== false
    mpp(static(false).name("a"), static(false).name("b")).parseBoolean("a && b") ==== false
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
