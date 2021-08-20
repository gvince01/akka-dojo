package ex5

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Sink, Source }
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestProbe
import akka.util.ByteString
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

class Ex5Spec extends AnyFlatSpec with CsvProcessor {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()
  val probe = TestProbe()

  val helloString: List[ByteString] = List(ByteString.fromString("hello"))
  val sourceUnderTest: Source[List[ByteString], NotUsed] = Source(List(helloString))

  it should "should correctly map a list of byte strings" in {
    sourceUnderTest
      .via(byteStringToStringFlow)
      .runWith(TestSink.probe[List[String]](probe.system)).request(1).expectNext(1 second, List("hello"))
  }

  it should "correctly filter out "

}
