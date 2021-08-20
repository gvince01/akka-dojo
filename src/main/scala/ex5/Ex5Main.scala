package ex5

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.alpakka.csv.scaladsl.{ CsvFormatting, CsvParsing }
import akka.stream.scaladsl.{ FileIO, Flow, Sink, Source }
import akka.util.ByteString

import java.nio.file.{ Path, Paths }
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Ex5Main extends App with CsvProcessor {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()
  val inputPath: Path = Paths.get("src/main/resources/ex5/country-list-corrupted.csv")
  val outputPath: Path = Paths.get("src/main/resources/ex5/country-list-fixed.csv")

      // use this code block to get your stream to terminate
//    .run
//    .andThen {
//      case _ =>
//        system.terminate()
//    }

}

trait CsvProcessor {

  // each row of the input CSV file will become a List[ByteString]
  // we need to convert the List[ByteString] into a List[String]
  // Use .utf8String to convert a ByteString into a String
  val inputCSVParser: Flow[ByteString, List[ByteString], NotUsed] = CsvParsing.lineScanner()

  val outputCSVFormatter: Flow[immutable.Iterable[String], ByteString, NotUsed] = CsvFormatting.format()

  def inputFileSource(inputPath: Path): Source[ByteString, Future[IOResult]] = {
    FileIO.fromPath(inputPath)
  }

  def outputFileWriter(outputPath: Path): Sink[ByteString, Future[IOResult]] = {
    FileIO.toPath(outputPath)
  }


}
