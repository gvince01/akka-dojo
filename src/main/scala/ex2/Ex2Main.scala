package ex2

import akka.actor.ActorSystem
import akka.stream.{ OverflowStrategy, ThrottleMode }
import akka.stream.scaladsl.Source

import scala.concurrent.duration.DurationInt

object Ex2Main extends App {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec = system.dispatcher

  // In this example we can see that the Source (producer) is producing elements
  // at a rate that the downstream can't handle

  // the buffer allows us to store elements from a faster upstream until it becomes full
  // We can define an overflow strategy to decide how to deal with a full buffer
  val source = Source(1 to 100)
    .map { x => println(s"Passing $x") ; x}
//    .buffer(5, OverflowStrategy.dropNew)
//    .buffer(5, OverflowStrategy.fail)
//    .buffer(5, OverflowStrategy.backpressure)
//    .buffer(5, OverflowStrategy.dropTail)
    .throttle(1, 1.second, 1, ThrottleMode.shaping)

  source.runForeach(println)

}


