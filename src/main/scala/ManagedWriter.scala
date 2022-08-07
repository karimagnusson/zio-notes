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