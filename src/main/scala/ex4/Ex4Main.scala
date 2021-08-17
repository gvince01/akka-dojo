package ex4

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Flow, Sink, Source }

object Ex4Main extends App {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()

  val source = Source(1 to 100)

  val flow1 = Flow[Int].map { input =>
    input.toString
  }

  val flow2 = Flow[String].map { input =>
    input.toSeq
  }

  val sink = Sink.foreach[Seq[Char]] {
    charSeq => println(charSeq.mkString("-"))
  }

  source
    .via(flow1)
    .via(flow2)
    .runWith(sink)

}
