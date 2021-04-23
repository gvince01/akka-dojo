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
code can be difficult and error prone (deadlocks anybody?!).



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

## RunnableGraph
                    
A Flow that has a Source and a Sink attached is ready to be `run()`.

```scala
// Create a source from an iterable
val source = Source(1 to 10)
// Create a sink that returns a 
val sink = Sink.fold[Int, Int](0)(_ + _)

// Connect the source to the sink - obtaining a RunnableGraph
val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

// materialize the flow and get the value
val sum: Future[Int] = runnable.run()
```
        
In the above example we are using `toMat` to indicate that we want to transfer the materialized value of the source and
sink. We are also using the `Keep.right` to say that we are only interested in the materialized value of the sink. 
                    

### Give it a go!
                        
See Ex1 for a more real world example of using a Source, Sink and Flow! Write a function to modify the input somehow and 
print the result to the console. What happens if you update the file while your code is running?
                   
      

## Backpressure 

Akka streams provides us with the ability to two asynchronous non-blocking back-pressured code. A user doesn't have to 
explicitly write back-pressure handling code -- it is build in and dealt with automatically.  
                  
