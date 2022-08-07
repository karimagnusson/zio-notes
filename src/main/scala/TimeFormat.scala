package io.github.karimagnusson.zio.notes

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter


private object TimeFormat {
  def apply(format: String): TimeFormat =
    new TimeFormatImpl(format)
}


private trait TimeFormat {
  def render(timestamp: Instant): String
}


private class TimeFormatImpl(format: String) extends TimeFormat {

  val formatter = DateTimeFormatter.ofPattern(format)

  def render(timestamp: Instant): String = {
    formatter.format(
      LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)
    )
  }
}