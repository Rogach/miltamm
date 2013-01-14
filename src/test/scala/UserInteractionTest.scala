package org.rogach.miltamm

import java.io.ByteArrayInputStream
import BuildImports._

class UserInteractionTest extends MiltammTest with CapturingTest {

  test ("asking user for a value") {
    Console.withIn(new ByteArrayInputStream("apple\n".getBytes)) {
      val (out, err, answer) = captureOutput {
        ask("What fruit?", x => Right(x))
      }
      answer ==== "apple"
      err ==== ""
      out ==== "What fruit?\n"
    }
  }
  
  test ("asking user for a value second time, if at first it didn't parse") {
    Console.withIn(new ByteArrayInputStream("da\nyes".getBytes)) {
      val (out, err, answer) = captureOutput {
        ask("fruit?", boolConverter)
      }
      answer ==== true
      err ==== ""
      out ==== "fruit?\nFailed to parse boolean value\nfruit?\n"
    }
  }
  
  test ("asking user for an integer value") {
    Console.withIn(new ByteArrayInputStream("42".getBytes)) {
      val (out, err, answer) = captureOutput {
        int("n?")()
      }
      answer ==== 42
      err ==== ""
      out ==== "n?\n"
    }
  }
  
  test ("value selects") {
    Console.withIn(new ByteArrayInputStream("2".getBytes)) {
      val (out, err, answer) = captureOutput {
        select("choose one:", "apple", "banana", "peach")()
      }
      answer ==== "banana"
      err ==== ""
      out ==== "choose one:\n 1 - apple\n 2 - banana\n 3 - peach\n"
    }
  }
  
  test ("if user enters empty string, ask again") {
    Console.withIn(new ByteArrayInputStream("\napple\n".getBytes)) {
      val (out, err, answer) = captureOutput {
        string("fruit?")()
      }
      answer ==== "apple"
      err ==== ""
      out ==== "fruit?\nfruit?\n"
    }
  }
  
  test ("regex matching") {
    Console.withIn(new ByteArrayInputStream("apple\n12\n".getBytes)) {
      val (out, err, answer) = captureOutput {
        string("n?", "\\d+".r)()
      }
      answer ==== "12"
      err ==== ""
      out ==== "n?\nFailed to parse, sorry. The value must match the regex: \\d+\nn?\n"
    }
  }

}
