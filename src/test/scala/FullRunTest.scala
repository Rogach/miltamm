package org.rogach.miltamm

import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import collection.JavaConversions._

/** Test harness to run the templates in src/templates-test/
  */
class FullRunTest extends MiltammTest with CapturingTest {
  (new File("src/templates-test")).listFiles.filter(_.isDirectory).filter(!_.getName.endsWith(".result")).foreach { testDir =>
    test ("running: %s" format testDir.getName) {
      val outputFile = new File(testDir + ".output")
      val expectedOutput = if (outputFile.exists) FileUtils.readFileToString(outputFile) else ""
      val inputFile = new File(testDir + ".input")
      val input = if (inputFile.exists) FileUtils.readFileToString(inputFile) else ""
      val (output, err, exits) = try { 
        captureOutput {
          withInput(input) {
            trapExit {
              Main.run(Seq("--silent", testDir.toString, "target/template-output/" + testDir.getName))
            }
          }
        }
      } catch {
        case e: Throwable =>
          e.printStackTrace
          null
      }
      FileUtils.write(new File("target/template-output/" + testDir.getName + ".output"), output)
      assert(exits == List(), "exit was thrown: " + exits)
      compareStrings(output, expectedOutput, "output difference:")
      compareDirs(new File(testDir + ".result"), new File("target/template-output/" + testDir.getName))
    }
  }
  
  def compareStrings(str1: String, str2: String, msg: String) = {
    assert(str1 == str2, msg + "\n>>>>>>>>>>>>>>>>>>>>\n" + str1 + "====================\n" + str2 + "<<<<<<<<<<<<<<<<<<<<")    
  }
  
  def compareDirs(dir1: File, dir2: File) = {
    def getFileNames(dir: File) = FileUtils.iterateFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).toList.map(_.getAbsolutePath.stripPrefix(dir.getAbsolutePath))
    val files1 = getFileNames(dir1)
    val files2 = getFileNames(dir2)
    files1.foreach { f1 =>
      assert(files2.find(f1==).isDefined, "file '%s' is missing from output" format f1)
      compareStrings(
        FileUtils.readFileToString(new File(dir2 + f1)), 
        FileUtils.readFileToString(new File(dir1 + f1)), 
        "file '%s' is different in output:" format f1)
    }
    files2.foreach { f2 =>
      assert(files1.find(f2==).isDefined, "file '%s' is new in output" format f2)
    }
  }
  
}
