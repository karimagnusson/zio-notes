package io.github.karimagnusson.zio.notes

import zio._


private[notes] class NotesApi(owner: String) {

  def info[R](text: String): RIO[Has[Notes], Unit] = {
    for {
      notes <- Notes.get
      _     <- notes.addInfo(owner, text)
    } yield ()
  }

  def warn[R](text: String): RIO[Has[Notes], Unit] = {
    for {
      notes <- Notes.get
      _     <- notes.addWarn(owner, text)
    } yield ()
  }

  def error[R](th: Throwable): RIO[Has[Notes], Unit] = {
    for {
      notes <- Notes.get
      _     <- notes.addError(owner, th)
    } yield ()
  }

  def debug[R](text: String): RIO[Has[Notes], Unit] = {
    for {
      notes <- Notes.get
      _     <- notes.addDebug(owner, text)
    } yield ()
  }
}