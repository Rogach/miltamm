package org.rogach.miltamm

import java.io.ByteArrayInputStream
import BuildImports._

class UserInteractionTest extends MiltammTest with CapturingTest {

  test ("asking user for a value") {
    withInput("apple\n") {
      val (out, err, answer) = captureOutput {
        ask("What fruit?", conv = x => Right(x))
      }
      answer ==== "apple"
      err ==== ""
      out ==== "What fruit?\n"
    }
  }
  
  test ("asking user for a value second time, if at first it didn't parse") {
    withInput("da\nyes") {
      val (out, err, answer) = captureOutput {
        ask("fruit?", conv = boolConverter)
      }
      answer ==== true
      err ==== ""
      out ==== "fruit?\nFailed to parse boolean value\nfruit?\n"
    }
  }
  
  test ("asking user for an integer value") {
    withInput("42") {
      val (out, err, answer) = captureOutput {
        int("n?")()
      }
      answer ==== 42
      err ==== ""
      out ==== "n?\n(integer value): "
    }
  }
  
  test ("value selects") {
    withInput("2") {
      val (out, err, answer) = captureOutput {
        select("choose one:", ("a" -> "apple"), ("b" -> "banana"), ("p" -> "peach"))()
      }
      answer ==== "b"
      err ==== ""
      out ==== "choose one:\n 1 - apple\n 2 - banana\n 3 - peach\n(1-3): "
    }
  }
  
  test ("if user enters empty string, ask again") {
    withInput("\napple\n") {
      val (out, err, answer) = captureOutput {
        string("fruit?")()
      }
      answer ==== "apple"
      err ==== ""
      out ==== "fruit?\n> fruit?\n> "
    }
  }
  
  test ("regex matching") {
    withInput("apple\n12\n") {
      val (out, err, answer) = captureOutput {
        string("n?", "", "\\d+".r)()
      }
      answer ==== "12"
      err ==== ""
      out ==== "n?\nFailed to parse, sorry. The value must match the regex: \\d+\nn?\n"
    }
  }
  
  test ("asking for keys while resolving build template") {
    withInput("42\n") {
      val (out, err, _) = captureOutput {
        val build = BuildCompiler.compile[BuildTemplate]("""val num = int("n?")""")
        build.resolveKeys()
      }
      err ==== ""
      out ==== "n?\n(integer value): "
    }
  }

}
