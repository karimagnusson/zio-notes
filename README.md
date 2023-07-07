# zio-notes

zio-notes is a simple logging library. It is easy to set up and needs minimal config. On startup the log files are created. If they exist, they are first deleted. The log entry is handled in a different fiber so your code does not have to wait until the entry is written to the file.

Availavle for ZIO 1 and ZIO 2.

note.print takes Any. If a Throwable is passed, the stacktrace is printed and if it is an object, toString is called on it.


#### Sbt
```sbt
// compiled for Scala 2.13.8 and ZIO 1.0.18
libraryDependencies += "io.github.karimagnusson" % "zio-notes" % "1.0.0"

// compiled for Scala 2.13.8 and ZIO 2.0.12
libraryDependencies += "io.github.karimagnusson" % "zio-notes" % "2.0.0"
```

#### Example
```scala
import zio._
import io.github.karimagnusson.zio.notes._

object Example extends zio.App {

  val job = for {
    _ <- note.info("App has started")
    _ <- note.debug("This part of the code runs")
    _ <- note.warn("Something is about to go wrong")
    _ <- note.error(new Exception("Something went wrong"))
    _ <- note.print("Of interest")
    _ <- note.print(new Exception("Well ..."))
  } yield ()

  val notesLayer = Notes.layer("/path/to/my/project/notes")

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    job.provideCustomLayer(notesLayer).exitCode
  }
}
```

#### Settings
You can turn off logging for debug and print.
And change the time format.
```scala
Notes.setDebug(false) // default: true
Notes.setTimeFormat("yyyy-MM-dd HH:mm:ss.SSS") // default: dd.MM.yyyy HH:mm:ss.SSS
val notesLayer = Notes.layer("/path/to/my/project/notes")
```

#### SelfNotes
If you wish to know which class was responsible for a certain log entry, you can extend your class with the SelfNotes trait.
```scala
class MyClass extends SelfNotes {
  val job = for {
    _ <- note.info("App has started") // 07.08.2022 07:09:38.484 [MyClass] App has started
  } yield ()
}
```

#### Custom owner
```scala
object Example extends zio.App {
  val note = Notes.forOwner("Example")
  val job = for {
    _ <- note.info("App has started") // 07.08.2022 07:09:38.484 [Example] App has started
  } yield ()
}
```


