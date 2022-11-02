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


private object LineWriter {
  def apply(dir: String, name: String) =
    new LineWriter(Paths.get(dir, name).toFile)
}


private class LineWriter(file: File) {

  def write(line: String): RIO[Any, Unit] = ZIO.attemptBlocking {
    val handle = new FileWriter(file, true)
    handle.write(line)
    handle.close()
  }

  def createFresh: RIO[Any, Unit] = ZIO.attemptBlocking {
    if (file.exists)
      file.delete()
    file.createNewFile()
  }
}



















