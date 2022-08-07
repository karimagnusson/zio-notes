# zio-notes

Zio-notes is a light-weight logging library. 

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
  } yield ()

  val notesLayer = Notes.layer(dir)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    job.provideCustomLayer(notesLayer).exitCode
  }
}
```

#### Settings
You can turn off logging for debug and change the time format.
```scala
Notes.setDebug(false) // default: true
Notes.setTimeFormat("yyyy-MM-dd HH:mm:ss.SSS") // default: dd.MM.yyyy HH:mm:ss.SSS
val notesLayer = Notes.layer(dir)
```


#### SelfNotes
If you wish to know wich class was responsible for a certain log entry, you can extend your class with the SelfNotes trait.
```scala
class MyClass extends SelfNotes {
  val job = for {
    _ <- note.info("App has started") // 07.08.2022 07:09:38.484 [MyClass] App has started
  } yield ()
}
```


