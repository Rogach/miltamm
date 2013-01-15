package org.rogach.miltamm

import BuildImports._

class KeysTest extends MiltammTest {

  test ("static keys - value") {
    val a = static(1)
    a() ==== 1
  }

  test ("static keys - map") {
    val a = static(1)
    val b = a.map(1+)
    b() ==== 2
  }

  test ("static keys - for comprehension (map)") {
    val a = static(1)
    val b = for (vA <- a) yield vA + 1
    b() ==== 2
  }

  test ("static keys - for comprehension (flatMap)") {
    val a = static(1)
    val b = static(2)
    val c = for {vA <- a; vB <- b} yield vA + vB
    c() ==== 3
  }

  test ("resolution of static keys in templates") {
    object B extends BuildTemplate {
      val a = static(1)
    }
    B.a.isDefined ==== false
    B.a.name ==== ""
    B.resolveKeys()
    B.a.isDefined ==== true
    B.a.name ==== "a"
  }
  
}
