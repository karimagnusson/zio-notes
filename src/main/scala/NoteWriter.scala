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

import java.time.Instant
import zio._
import zio.blocking._


private object NoteWriter {

  def create(dir: String, format: String): RIO[Blocking, NoteWriter] = for {
    writer  <- Task.effect(new NoteWriterImpl(dir, format))
    _       <- writer.createFreshFiles
  } yield writer
}


private trait NoteWriter {
  def write(note: NoteType): RIO[Blocking, Unit]
  def createFreshFiles: RIO[Blocking, Unit]
}


private class NoteWriterImpl(dir: String, format: String) extends NoteWriter {

  val timeFormat = TimeFormat(format)

  val manInfo = ManagedWriter.fromDir(dir, "info.log")
  val manWarn = ManagedWriter.fromDir(dir, "warn.log")
  val manError = ManagedWriter.fromDir(dir, "error.log")
  val manDebug = ManagedWriter.fromDir(dir, "debug.log")

  def makeLine(iso: String, owner: String, text: String) = Task.effect {
    s"$iso [$owner] $text\n"
  }
  
  def write(note: NoteType): RIO[Blocking, Unit] = note match {
    case InfoNote(owner, text) => for {
      time  <- Task.effect(timeFormat.now)
      line  <- makeLine(time, owner, text)
      _     <- manInfo.write(line)
    } yield ()
    case WarnNote(owner, text) => for {
      time  <- Task.effect(timeFormat.now)
      line  <- makeLine(time, owner, text)
      _     <- manWarn.write(line)
    } yield ()
    case ErrorNote(owner, th) => for {
      time  <- Task.effect(timeFormat.now)
      trace <- Task.effect(Stacktrace.render(th))
      line  <- makeLine(time, owner, trace)
      _     <- manError.write(line)
    } yield ()
    case DebugNote(owner, text) => for {
      time  <- Task.effect(timeFormat.now)
      line  <- makeLine(time, owner, text)
      _     <- manDebug.write(line)
    } yield ()
  }

  def createFreshFiles: RIO[Blocking, Unit] = for {
    _ <- manInfo.createFresh
    _ <- manWarn.createFresh
    _ <- manError.createFresh
    _ <- manDebug.createFresh
  } yield ()
}



