package ex2

import akka.actor.ActorSystem
import akka.stream.ThrottleMode
import akka.stream.scaladsl.Source

import scala.concurrent.duration.DurationInt

object Ex2aMain extends App {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec = system.dispatcher

  // In this example we can see that the Source (producer) is producing elements
  // at a rate that the downstream can't handle (we are using throttle to simulate a slow downstream)
  val source = Source(1 to 100)
    .map { x => println(s"Passing $x") ; x}
    .throttle(1, 1.second, 1, ThrottleMode.shaping)

  source.runForeach(println)

}


