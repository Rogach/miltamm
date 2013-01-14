package org.rogach.miltamm

import java.io.ByteArrayInputStream

class UserInteractionTest extends MiltammTest with CapturingTest {

  test ("asking user for a value") {
    Console.withIn(new ByteArrayInputStream("apple\n".getBytes)) {
      var answer = ""
      val (out, err) = captureOutput {
        answer = BuildImports.ask("What fruit?", identity)
      }
      answer ==== "apple"
      err ==== ""
      out ==== "What fruit?\n"
    }
  }

}
