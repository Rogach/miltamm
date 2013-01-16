package org.rogach.miltamm

class PathsTest extends MiltammTest {
  test ("no transform") {
    Path("apples") ==== Seq("apples")
  }
  test ("no transform with '/' after name") {
    Path("apples/") ==== Seq("apples")
  }
  test ("no transform with '/' before name") {
    Path("/apples") ==== Seq("apples")
  }
  test ("splitting a path") {
    Path("tree/apples") ==== Seq("tree", "apples")
  }
  test ("splitting a path with '/' before it") {
    Path("/tree/apples") ==== Seq("tree", "apples")
  }
  test ("splitting a path with '/' after it") {
    Path("tree/apples/") ==== Seq("tree", "apples")
  }
  test ("splitting a path with '//' in it") {
    Path("tree//apples") ==== Seq("tree", "apples")
  }
  test ("appending to a path") {
    Path("tree") ++ Path("apples") ==== Seq("tree", "apples")
  }
}
