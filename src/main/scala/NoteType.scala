package io.github.karimagnusson.zio.notes


private sealed trait NoteType

private case class InfoNote(
  owner: String,
  text: String
) extends NoteType

private case class WarnNote(
  owner: String,
  text: String
) extends NoteType

private case class ErrorNote(
  owner: String,
  th: Throwable
) extends NoteType

private case class DebugNote(
  owner: String,
  text: String
) extends NoteType
