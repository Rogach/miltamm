package org.rogach.miltamm

import BuildImports._

class MppTest extends MiltammTest {
  test("empty") {
    mpp().process(Nil) ==== Nil
  }
  test("pass through") {
    mpp().process(Seq("apples")) ==== Seq("apples")
    mpp().process(Seq("apples", "bananas")) ==== Seq("apples", "bananas")
  }
  test("replacing keys in strings") {
    mpp(static("rogach").name("name")).process(Seq("org.#{name}.miltamm")) ==== Seq("org.rogach.miltamm")
  }
  test("if - true") {
    mpp().process(Seq("#if true", "apples", "#fi")) ==== Seq("apples")
  }
  test("if - false") {
    mpp().process(Seq("#if false", "apples", "#fi")) ==== Seq()
  }
  test("if/else - true") {
    mpp().process(Seq("#if true", "apples", "#else", "bananas", "#fi")) ==== Seq("apples")
  }
  test("if/else - false") {
    mpp().process(Seq("#if false", "apples", "#else", "bananas", "#fi")) ==== Seq("bananas")
  }
  test("if/elif - true/true") {
    mpp().process(Seq("#if true", "apples", "#elif true", "bananas", "#fi")) ==== Seq("apples")
  }
  test("if/elif - false/true") {
    mpp().process(Seq("#if false", "apples", "#elif true", "bananas", "#fi")) ==== Seq("bananas")
  }
  test("if/elif - false/false") {
    mpp().process(Seq("#if false", "apples", "#elif false", "bananas", "#fi")) ==== Seq()
  }
  test("if/elif/else - false/false") {
    mpp().process(Seq("#if false","apples","#elif false","bananas","#else","coconut","#fi")) ==== Seq("coconut")
  }
  test("if/elif/elif - false/false/true") {
    mpp().process(Seq("#if false","apples","#elif false","bananas","#elif true","coconut","#fi")) ==== Seq("coconut")
  }
  test("two ifs in a row") {
    mpp().process(Seq("#if false", "apples", "#fi", "#if true", "bananas", "#fi")) ==== Seq("bananas")
  }

}
