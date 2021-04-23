name := "akka-dojo"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.9",
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "2.0.2",
)
