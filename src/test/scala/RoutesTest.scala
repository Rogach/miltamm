package org.rogach.miltamm


class RoutesTest extends MiltammTest {
  import BuildImports.{ignore => ign, _}

  type RT = PartialFunction[Path, Action]

  test ("empty copy") {
    val route: RT = ""
    route("apples") ==== Action("", None, Some(""))
  }
  test ("non-empty copy") {
    val route: RT = "tree"
    route("tree/apples") ==== Action("tree", None, Some("tree"))
  }
  test ("move") {
    val route: RT = "tree" >> "palm"
    route("tree/apples") ==== Action("tree", None, Some("palm"))
  }
  test ("nested move") {
    val route: RT = "tree" >> "palm" append Seq("apple" >> "banana")
    route("tree/apple") ==== Action("tree/apple", None, Some("palm/banana"))
  }
  test ("ignore") {
    val route: RT = ign("tree")
    route("tree/apple") ==== Action("tree", None, None)
  }
  test ("unignore") {
    val route: RT = ign("tree") append Seq("apple")
    route("tree/apple") ==== Action("tree/apple", None, Some("tree/apple"))
  }
  test ("replace key values in move operation") {
    currentConf.withValue(Conf(keys = Seq(static("peach").name("fruit")))) {
      val route: RT = "tree/apple" >> "tree/#{fruit}"
      route("tree/apple") ==== Action("tree/apple", None, Some("tree/peach"))
    }
  }
  test ("optional ignore - true") {
    val k = static(true)
    val route: RT = iff(k) { "tree/apple" >> "tree/banana" }
    route("tree/apple") ==== Action("tree/apple", None, Some("tree/banana"))
  }
  test ("optional ignore - false") {
    val k = static(false)
    val route: RT = iff(k) { "tree/apple" >> "tree/banana" }
    route("tree/apple") ==== Action("tree/apple", None, None)
  }

}
