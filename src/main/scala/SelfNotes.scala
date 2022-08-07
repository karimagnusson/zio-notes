package io.github.karimagnusson.zio.notes


trait SelfNotes {
  val note = Notes.forOwner(this.getClass.getName)
}