package io.github.karimagnusson.zio.notes

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private object TimeFormat {
  def apply(format: String): TimeFormat =
    new TimeFormatImpl(format)
}


private trait TimeFormat {
  def now: String
}


private class TimeFormatImpl(format: String) extends TimeFormat {
  val formatter = DateTimeFormatter.ofPattern(format)
  def now: String = formatter.format(LocalDateTime.now)
}