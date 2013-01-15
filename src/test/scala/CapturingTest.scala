package org.rogach.miltamm

import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.lang.{System, SecurityManager, SecurityException}
import java.security.Permission

trait CapturingTest {
  /** Captures all output from the *fn* block into two strings - (stdout, stderr). */
  def captureOutput[A](fn: => A):(String, String, A) = {
    val normalOut = Console.out
    val normalErr = Console.err
    val streamOut = new ByteArrayOutputStream()
    val streamErr = new ByteArrayOutputStream()
    Console.setOut(streamOut)
    Console.setErr(streamErr)
    val res = fn
    Console.setOut(normalOut)
    Console.setErr(normalErr)
    (streamOut.toString, streamErr.toString, res)
  }
  
  /** Supresses exit in *fn* block. Returns list of exit statuses that were attempted. */
  def trapExit(fn: => Unit):List[Int] = {
    @volatile var statuses = List[Int]()
    val normalSM = System.getSecurityManager
    object SM extends SecurityManager {
      override def checkExit(status:Int) {
        statuses ::= status
        throw new SecurityException
      }
      override def checkPermission(p:Permission) {}
    }
    System.setSecurityManager(SM)
    try {
      fn
    } catch { 
      case e:SecurityException => 
    }
    System.setSecurityManager(normalSM)
    statuses.reverse
  }
  
  /** Supresses exits in *fn* block, and captures stdout/stderr. */
  def captureOutputAndExits(fn: => Unit): (String, String, List[Int]) = {
    captureOutput {
      trapExit(fn)
    }
  }

  /** Runs program with needed input. */
  def withInput[A](input: String)(fn: => A): A = {
    Console.withIn(new ByteArrayInputStream(input.getBytes))(fn)
  }
  
}
