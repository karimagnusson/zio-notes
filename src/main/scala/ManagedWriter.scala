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

import java.io.{File, FileWriter}
import java.nio.file.{Path, Paths}
import zio._
import zio.blocking._


private object ManagedWriter {

  def apply(file: File): ManagedWriter =
    new ManagedWriterImpl(file)

  def fromPath(path: Path): ManagedWriter =
    apply(path.toFile)

  def fromDir(dir: String, name: String): ManagedWriter =
    fromPath(Paths.get(dir, name))
}

trait ManagedWriter {
  def write(line: String): RIO[Blocking, Unit]
  def createFresh: RIO[Blocking, Unit]
}


private class ManagedWriterImpl(file: File) extends ManagedWriter {

  val man = Managed.fromAutoCloseable(
    ZIO.effectTotal(
      new FileWriter(file, true)
    )
  )

  def write(line: String): RIO[Blocking, Unit] = {
    man.use(writer => effectBlocking(writer.write(line)))
  }

  def createFresh: RIO[Blocking, Unit] = effectBlocking {
    if (file.exists)
      file.delete()
    file.createNewFile()
  }
}