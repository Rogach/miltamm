package org.rogach.miltamm

class PathsTest extends MiltammTest {
  test ("no transform") {
    Path("apples").p ==== List("apples")
  }
  test ("no transform with '/' after name") {
    Path("apples/").p ==== List("apples")
  }
  test ("no transform with '/' before name") {
    Path("/apples").p ==== List("apples")
  }
  test ("splitting a path") {
    Path("tree/apples").p ==== List("tree", "apples")
  }
  test ("splitting a path with '/' before it") {
    Path("/tree/apples").p ==== List("tree", "apples")
  }
  test ("splitting a path with '/' after it") {
    Path("tree/apples/").p ==== List("tree", "apples")
  }
  test ("splitting a path with '//' in it") {
    Path("tree//apples").p ==== List("tree", "apples")
  }
  test ("appending to a path") {
    Path("tree").append(Path("apples")).p ==== List("tree", "apples")
  }
  test ("prepending to a path") {
    Path("apples").prepend(Path("tree")).p ==== List("tree", "apples")
  }
}
