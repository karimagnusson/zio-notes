/*
* Copyright 2021 Kári Magnússon
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package io.github.karimagnusson.zio.notes

import zio._
import zio.blocking._


object Notes {

  private var debug = true
  private var format = "dd.MM.yyyy HH:mm:ss.SSS"

  def setDebug(value: Boolean): Unit = {
    debug = value
  }

  def setTimeFormat(value: String): Unit = {
    format = value
  }

  def forOwner(owner: String) = new NotesApi(owner)

  private def create(dir: String): RIO[Blocking, Notes] = {
    for {
      writer <- NoteWriter.create(dir, format)
      queue  <- Queue.unbounded[NoteType]
      loop    = for {
        note   <- queue.take
        line   <- writer.write(note)
      } yield ()
      fiber  <- loop.forever.fork
    } yield new NotesImpl(queue, fiber, debug)
  }

  def layer(dir: String): ZLayer[Blocking, Throwable, Has[Notes]] = {    
    ZLayer.fromAcquireRelease(create(dir))(_.close)
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
    _ <- queue.offer(InfoNote(owner, text))
  } yield ()

  def addWarn(owner: String, text: String): UIO[Unit] = for {
    _ <- queue.offer(WarnNote(owner, text))
  } yield ()

  def addError(owner: String, th: Throwable): UIO[Unit] = for {
    _ <- queue.offer(ErrorNote(owner, th))
  } yield ()

  def addDebug(owner: String, text: String): UIO[Unit] = {
    if (debug)
      queue.offer(DebugNote(owner, text)).map(_ => ())
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



























