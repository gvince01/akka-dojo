package ex2

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source

object Ex2Main extends App {

  // Create an actor system which we can use to execute our code
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec = system.dispatcher

  val source = Source(1 to 100)
    .map { x => println(s"Passing $x") ; x}

  source.runForeach(println)

}


