package org.rogach.miltamm

import java.io.ByteArrayInputStream

class UserInteractionTest extends MiltammTest with CapturingTest {

  test ("asking user for a value") {
    Console.withIn(new ByteArrayInputStream("apple\n".getBytes)) {
      val (out, err, answer) = captureOutput {
        BuildImports.ask("What fruit?", x => Right(x))
      }
      answer ==== "apple"
      err ==== ""
      out ==== "What fruit?\n"
    }
  }
  
  test ("asking user for a value second time, if at first it didn't parse") {
    Console.withIn(new ByteArrayInputStream("da\nyes".getBytes)) {
      val (out, err, answer) = captureOutput {
        BuildImports.ask("fruit?", BuildImports.boolConverter)
      }
      answer ==== true
      err ==== ""
      out ==== "fruit?\nFailed to parse boolean value\nfruit?\n"
    }
  }

}
