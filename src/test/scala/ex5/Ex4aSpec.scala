package ex5

import akka.actor.ActorSystem
import akka.pattern.pipe
import akka.stream.scaladsl.{ Sink, Source }
import akka.testkit.TestProbe
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class Ex4aSpec extends AnyFlatSpec {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()

  it should "should correctly group the elements from the source" in {

    val sourceUnderTest = Source(1 to 4).grouped(2)

    val probe = TestProbe()
    sourceUnderTest.runWith(Sink.seq).pipeTo(probe.ref)
    probe.expectMsg(3.seconds, Seq(Seq(1, 2), Seq(3, 4)))
  }

}
