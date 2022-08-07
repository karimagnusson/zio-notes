package io.github.karimagnusson.zio.notes

import java.io.{PrintWriter, CharArrayWriter}


private object Stacktrace {

  val wrapThrowable: Throwable => Exception = {
    case ex: Exception => ex
    case th: Throwable => new Exception(th)
  }

  def render(th: Throwable) = {
    val ex = wrapThrowable(th)
    val cw = new CharArrayWriter()
    val pw = new PrintWriter(cw)
    ex.printStackTrace(pw)
    pw.close()
    cw.toString
  }
}