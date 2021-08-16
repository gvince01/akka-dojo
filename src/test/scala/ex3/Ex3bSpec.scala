package ex3

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Sink, Source }
import akka.testkit.TestProbe
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration.DurationInt

class Ex3bSpec extends AnyFlatSpec {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()

  it should "correctly generate a tick" in {

    case object Tick
    val sourceUnderTest = Source.tick(0.seconds, 200.millis, Tick)

    val probe = TestProbe()
    val cancellable = sourceUnderTest
      .to(Sink.actorRef(probe.ref, onCompleteMessage = "completed", onFailureMessage = _ => "failed"))
      .run()

    probe.expectMsg(1.second, Tick)
    probe.expectNoMessage(100.millis)
    probe.expectMsg(3.seconds, Tick)
    cancellable.cancel()
    probe.expectMsg(3.seconds, "completed")
  }

}