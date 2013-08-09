package org.rogach.miltamm

import java.io.ByteArrayInputStream
import BuildImports._

class UserInteractionTest extends MiltammTest with CapturingTest {

  test ("asking user for a value") {
    withInput("apple\n") {
      val (out, err, answer) = captureOutput {
        ask("What fruit?", "banana", conv = x => Right(x))
      }
      answer ==== "apple"
      err ==== ""
      out ==== "What fruit?\n[banana]: "
    }
  }

  test ("asking user for a value second time, if at first it didn't parse") {
    withInput("da\nyes") {
      val (out, err, answer) = captureOutput {
        ask("fruit?", "no", conv = boolConverter)
      }
      answer ==== true
      err ==== ""
      out ==== "fruit?\n[no]: Failed to parse boolean value\nfruit?\n[no]: "
    }
  }

  test ("asking user for an integer value") {
    withInput("42") {
      val (out, err, answer) = captureOutput {
        int("n?", 12)()
      }
      answer ==== 42
      err ==== ""
      out ==== "n?\n[12]: "
    }
  }

  test ("value selects") {
    withInput("2") {
      val (out, err, answer) = captureOutput {
        select("choose one:", ("a" -> "apple"), ("b" -> "banana"), ("p" -> "peach"))()
      }
      answer ==== "b"
      err ==== ""
      out ==== "choose one:\n 1 - apple\n 2 - banana\n 3 - peach\n[1]: "
    }
  }

  test ("if user enters empty string, use the default") {
    withInput("\n") {
      val (out, err, answer) = captureOutput {
        string("fruit?", "banana")()
      }
      answer ==== "banana"
      err ==== ""
      out ==== "fruit?\n[banana]: "
    }
  }

  test ("regex matching") {
    withInput("apple\n12\n") {
      val (out, err, answer) = captureOutput {
        string("n?", "", "\\d+".r)()
      }
      answer ==== "12"
      err ==== ""
      out ==== "n?\n[]: Failed to parse, sorry. The value must match the regex: \\d+\nn?\n[]: "
    }
  }

  test ("asking for keys while resolving build template") {
    withInput("42\n") {
      val (out, err, _) = captureOutput {
        val build = BuildCompiler.compile("""val num = int("n?", 12)""")
        build.resolveKeys()
      }
      err ==== ""
      out ==== "n?\n[12]: "
    }
  }

  test ("caching the key value, not asking two times") {
    withInput("n\n") {
      val (out, err, res) = captureOutput {
        val a = bool("a?", true)
        a()
        a()
      }
      err ==== ""
      out ==== "a?\n[yes]: "
      res ==== false
    }
  }

  test ("don't ask the unneeded key") {
    val a = bool("a?", true)
    val b = bool("b?", true).when(a, or = false)
    withInput("n\nn\nn\n") {
      val (out, err, res) = captureOutput {
        a()
        b()
      }
      err ==== ""
      out ==== "a?\n[yes]: "
      res ==== false
    }
  }

}
