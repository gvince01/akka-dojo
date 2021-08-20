package ex1

import akka.actor.ActorSystem
import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.stream.scaladsl.{ Sink, Source }
import akka.{ Done, NotUsed }

import java.nio.file.Paths
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object Ex1Main extends App {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()

  val path = Paths.get("src/main/resources/ex1/text.txt")

  // The input file is our source
  val lines: Source[String, NotUsed] = FileTailSource.lines(
    path = path,
    maxLineSize = 8192,
    pollingInterval = 250.millis
  )

  val sink: Sink[String, Future[Done]] = Sink.foreach[String] { input =>
    println(myTransformationFunction(input))
  }


  // Create a function that transforms the string
  def myTransformationFunction(input: String): String = input.capitalize


  // lines.runWith is the same as saying
  // lines.toMat(sink)(Keep.right).run()
  lines.runWith(sink)
}
