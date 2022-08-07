package io.github.karimagnusson.zio.notes

import java.time.Instant
import zio._
import zio.blocking._


object Notes {

  def forOwner(owner: String) = new NotesApi(owner)

  private def create(
    dir: String,
    debug: Boolean,
    format: String
  ): RIO[Blocking, Notes] = {
    for {
      writer <- NoteWriter.effect(dir, format)
      queue  <- Queue.unbounded[NoteType]
      loop    = for {
        note   <- queue.take
        line   <- writer.write(note)
      } yield ()
      fiber  <- loop.forever.fork
    } yield new NotesImpl(queue, fiber, debug)
  }

  def layer(
    dir: String,
    debug: Boolean = true,
    format: String = "dd.MM.yyyy HH:mm:ss.SSS"
   ): ZLayer[Blocking, Throwable, Has[Notes]] = {    
    ZLayer.fromAcquireRelease(create(dir, debug, format))(_.close)
  }

  def get = ZIO.access[Has[Notes]](_.get)
}


trait Notes {
  def addInfo(owner: String, text: String): UIO[Unit]
  def addWarn(owner: String, text: String): UIO[Unit]
  def addError(owner: String, th: Throwable): UIO[Unit]
  def addDebug(owner: String, text: String): UIO[Unit]
  def close: UIO[Unit]
}


private class NotesImpl(
    queue: Queue[NoteType],
    fiber: Fiber.Runtime[Any, Unit],
    debug: Boolean
  ) extends Notes {

  def addInfo(owner: String, text: String): UIO[Unit] = for {
    _ <- queue.offer(InfoNote(Instant.now, owner, text))
  } yield ()

  def addWarn(owner: String, text: String): UIO[Unit] = for {
    _ <- queue.offer(WarnNote(Instant.now, owner, text))
  } yield ()

  def addError(owner: String, th: Throwable): UIO[Unit] = for {
    _ <- queue.offer(ErrorNote(Instant.now, owner, th))
  } yield ()

  def addDebug(owner: String, text: String): UIO[Unit] = {
    if (debug)
      queue.offer(DebugNote(Instant.now, owner, text)).map(_ => ())
    else
      ZIO.succeed(())
  }

  def close: UIO[Unit] = {
    for {
      _ <- queue.shutdown
      _ <- fiber.interrupt
    } yield ()
  }
}



























