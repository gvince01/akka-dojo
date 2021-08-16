package ex4

import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.stream.scaladsl.{ Sink, Source }

import java.nio.file.Paths
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class Ex4Main extends App {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()

  val path = Paths.get("src/main/resources/ex4/text.txt")

  // The input file is our source
  val lines: Source[String, NotUsed] = FileTailSource.lines(
    path = path,
    maxLineSize = 8192,
    pollingInterval = 250.millis
  )


  
}
