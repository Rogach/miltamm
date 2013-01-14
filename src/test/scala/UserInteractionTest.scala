package org.rogach.miltamm

import java.io.ByteArrayInputStream

class UserInteractionTest extends MiltammTest with CapturingTest {

  test ("asking user for a value") {
    Console.withIn(new ByteArrayInputStream("apple\n".getBytes)) {
      val (out, err, answer) = captureOutput {
        BuildImports.ask("What fruit?", identity)
      }
      answer ==== "apple"
      err ==== ""
      out ==== "What fruit?\n"
    }
  }

}
