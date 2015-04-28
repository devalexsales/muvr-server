import Dependencies._

Build.Settings.project

name := "common"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.persistence,
  spray.routing,
  spray.json,
  scalaz.core,
  cassandra_driver
)
