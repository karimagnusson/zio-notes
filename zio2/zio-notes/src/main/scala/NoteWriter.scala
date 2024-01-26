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


private object NoteWriter {

  def create(dir: String, format: String): RIO[Any, NoteWriter] = for {
    writer  <- ZIO.attempt(new NoteWriter(dir, format))
    _       <- writer.createFreshFiles
  } yield writer
}


private class NoteWriter(dir: String, format: String) {

  val timeFormat = TimeFormat(format)

  val infoFile = LineWriter(dir, "info.log")
  val warnFile = LineWriter(dir, "warn.log")
  val errorFile = LineWriter(dir, "error.log")
  val debugFile = LineWriter(dir, "debug.log")

  def makeLine(iso: String, owner: String, text: String) = ZIO.attempt {
    s"$iso [$owner] $text\n"
  }

  def consoleLine(owner: String, obj: Any) = ZIO.attempt {
    obj match {
      case s: String => "[%s] %s".format(owner, s)
      case th: Throwable => "[%s] %s".format(owner, Stacktrace.render(th))
      case o => "[%s] %s".format(owner, o.toString)
    }
  }
  
  def write(note: NoteType): URIO[Any, Unit] = (note match {
    
    case InfoNote(owner, text) => for {
      time  <- ZIO.attempt(timeFormat.now)
      line  <- makeLine(time, owner, text)
      _     <- infoFile.write(line)
    } yield ()
    
    case WarnNote(owner, text) => for {
      time  <- ZIO.attempt(timeFormat.now)
      line  <- makeLine(time, owner, text)
      _     <- warnFile.write(line)
    } yield ()
    
    case ErrorNote(owner, th) => for {
      time  <- ZIO.attempt(timeFormat.now)
      trace <- ZIO.attempt(Stacktrace.render(th))
      line  <- makeLine(time, owner, trace)
      _     <- errorFile.write(line)
    } yield ()
    
    case DebugNote(owner, text) => for {
      time  <- ZIO.attempt(timeFormat.now)
      line  <- makeLine(time, owner, text)
      _     <- debugFile.write(line)
    } yield ()

    case PrintNote(owner, obj) => for {
      time  <- ZIO.attempt(timeFormat.now)
      line  <- consoleLine(owner, obj)
      _     <- ZIO.attemptBlocking { println(line) }
    } yield ()
    
  }).catchAll {
    case th: Throwable =>
      ZIO.attemptBlocking( 
        println(Stacktrace.render(th))
      ).orDie
  }

  def createFreshFiles: RIO[Any, Unit] = for {
    _ <- infoFile.createFresh
    _ <- warnFile.createFresh
    _ <- errorFile.createFresh
    _ <- debugFile.createFresh
  } yield ()
}



