package io.github.karimagnusson.zio.notes

import java.time.Instant


private sealed trait NoteType

private case class InfoNote(
  time: Instant,
  owner: String,
  text: String
) extends NoteType

private case class WarnNote(
  time: Instant,
  owner: String,
  text: String
) extends NoteType

private case class ErrorNote(
  time: Instant,
  owner: String,
  th: Throwable
) extends NoteType

private case class DebugNote(
  time: Instant,
  owner: String,
  text: String
) extends NoteType
