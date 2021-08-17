name := "akka-dojo"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.9",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.9" % Test,
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "2.0.2",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "3.0.3",
  "org.scalatest" %% "scalatest" % "3.2.0" % Test
)
