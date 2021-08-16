# Akka Streams

## Introduction

This dojo aims to introduce Akka Streams and gives some hands on experience using them.

Before we go any further it's important to understand the issues that Akka aims to solve. The first thing to do is to
define a couple of key terms:

- Concurrency - Is the occurrence of multiple events within overlapping time frames, but not simultaneously.

- Parallelism - If the concurrent model of task execution never involves two tasks overlapping in execution then parallelism is the
  opposite i.e. the tasks entirely overlap.

### Dangers of Concurrent Programming

Because of the threat of concurrent access to things like state, we have to protect our code by synchronizing access
to that state. This is to ensure that only one thread at a time is executing a
block of code. In order to do this a common technique is the use of locks. Unfortunately, instead of executing code,
threads spend most of their time competing for the lock (and not doing work). Furthermore, writing good, safe synchronized
code can be difficult and error-prone (deadlocks anybody?!).


## Akka Streams     

There is some basic terminology that we must define before delving into Akka streams.

- **Stream**: An active process that involves moving and transforming data

- **Graph**: The pathways through which elements shall flow when the stream is running.

- **Non-blocking**: A certain operation does not hinder the progress of the calling thread, even if it takes a long time 
  to finish the requested operation.

- **Back-pressure**: A means of flow-control, a way for consumers of data to notify a producer about their current 
  availability, effectively slowing down the upstream producer to match their consumption speeds.
                                                                                                  

### Source

A source is an operator with _exactly one output_. It emits data elements whenever downstream operators are ready to 
receive them.

Some examples of a source are:

```scala
// Create a source from an Iterable
Source(List(1, 2, 3))

// Create a source from a Future
Source.future(Future.successful("Hello World!"))
```

### Sink

A Sink is an operator with _exactly one input_. It requests and accepts data elements, possibly slowing down the 
upstream producer of elements.

Some examples of a Sink are:
```scala
// Sink that folds over the stream and returns a Future
// of the final result as its materialized value
Sink.fold[Int, Int](0)(_ + _)

// A Sink that executes a side-effecting call for every element of the stream
Sink.foreach[String](println(_))
```

### Flow
                   
An operator which has exactly _one input and output_, which connects its upstream and downstream by transforming the
data elements flowing through it.


```scala
// Accepts an Int and returns an Int
Flow[Int].map(_ * 2)
```
                 
### Materializer

We can use one if we want our Flow to have some side effects like logging or saving results.
Most commonly, we will be passing the NotUsed alias as a Materializer to denote that our Flow should not have any side effects.

## RunnableGraph
                    
A Flow that has a Source and a Sink attached is ready to be `run()`.

```scala
// Create a source from an iterable
val source = Source(1 to 10)
// Create a sink that returns an Int
val sink = Sink.fold[Int, Int](0)(_ + _)

// Connect the source to the sink - obtaining a RunnableGraph
val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

// materialize the flow and get the value
val sum: Future[Int] = runnable.run()
```
        
In the above example we are using `toMat` to indicate that we want to transfer the materialized value of the source and
sink. We are also using the `Keep.right` to say that we are only interested in the materialized value of the sink. 
                    

### Give it a go!
                        
See `Ex1` for a more real world example of using a Source, Sink and Flow! Write a function to modify the input somehow and 
print the result to the console. What happens if you update the file and save it while your code is running?
                   

## Backpressure 

Akka streams provides us with the ability to two asynchronous non-blocking back-pressured code. A user doesn't have to 
explicitly write back-pressure handling code -- it is build in and dealt with automatically. 

To highlight backpressure, lets have a look at the examples in the ex2 package.

First, let run `Ex2Main` and see what happens when the `Sink` can handle the rate at which the source is producing elements. 
                         
```scala
val source = Source(1 to 100)
  .map { x => println(s"Passing $x") ; x}

source.runForeach(println)
```

We should see that the Sink consumes the elements from the source when they are produced. 
                                                                                             

In most real world cases we will want to perform some sort of computation on the data that's being produced from the Source. Often, 
these computations will take longer to execute that it takes for the Source to produce elements. In these instances, the Source will produce
elements at a faster rate than the Sink can consume them.

Now, let's have a look at `Ex2aMain`,    

```scala
// In this example we can see that the Source (producer) is producing elements
// at a rate that the downstream can't handle (we are using throttle to simulate a slow downstream)
val source = Source(1 to 100)
  .map { x => println(s"Passing $x") ; x}
  .throttle(1, 1.second, 1, ThrottleMode.shaping)

source.runForeach(println)
```
           
Compare the output of this exercise to the previous. You should notice that the Source only passes elements to the Sink 
when the Sink is ready to receive them. This is an example of backpressure. 


Depending on the nature of what we are processing, we might want our application to apply backpressure in different ways.
If we look at `Ex2bMain`, we can see that we've added a buffer. 

A buffer allows a faster upstream to progress independently of a slower subscriber by aggregating elements into batches 
until the subscriber is ready to accept them. 

```scala
// the buffer allows us to store elements from a faster upstream until it becomes full
// We can define an overflow strategy to decide how to deal with a full buffer
val source = Source(1 to 100)
  .map { x => println(s"Passing $x") ; x}
  .buffer(5, OverflowStrategy.dropHead)
//    .buffer(5, OverflowStrategy.dropTail)
//    .buffer(5, OverflowStrategy.dropBuffer)
//    .buffer(5, OverflowStrategy.dropNew)
//    .buffer(5, OverflowStrategy.backpressure)
//    .buffer(5, OverflowStrategy.fail)
  .throttle(1, 1.second, 1, ThrottleMode.shaping)

source.runForeach(println)
```

Try running the code using the different buffer types to get an understanding of what's going on.


## Testing

Similar to the akka-testkit, the `akka-streams-testkit` provides us with a `TestProbe`. For example, in `Ex3aSpec` we
are materilizing out Stream into a `Future` (see scala doc for `Sink.seq` for more information) and then using a pipe
to pass the result into our test probe,

```scala
val sourceUnderTest = Source(1 to 4).grouped(2)

val probe = TestProbe()
sourceUnderTest.runWith(Sink.seq).pipeTo(probe.ref)
probe.expectMsg(3.seconds, Seq(Seq(1, 2), Seq(3, 4)))

```

If we now compare the previous example with the one in `Ex3bSpec`, we can see that we don't materialize our stream into a future.
We instead send our incoming elements to a given ActorRef. This allows us to use the assertion methods on the messages one by one as they arrive (as opposed to the previous example 
whereby we created an assertion one the final result).

```scala
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
```

### Streams Testkit


The testkit module comes with two main components that are `TestSource` and `TestSink` which provide sources and sinks that materialize to 
probes. 

For example, we can use the `TestSink` in the following example:

```scala
val sourceUnderTest = Source(1 to 4).filter(_ % 2 == 0).map(_ * 2)

sourceUnderTest.runWith(TestSink[Int]()).request(2).expectNext(4, 8).expectComplete()
```
                                                                
Similarly, we can use the `TestSource`,

```scala
val sinkUnderTest = Sink.head[Int]

val (probe, future) = TestSource.probe[Int].toMat(sinkUnderTest)(Keep.both).run()
probe.sendError(new Exception("failure"))

assert(future.failed.futureValue.getMessage == "failure")
```
        

## Putting it all together

