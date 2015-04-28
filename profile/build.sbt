import Dependencies._

Build.Settings.project

name := "profile"

libraryDependencies ++= Seq(
  akka.actor,
  akka.cluster,
  akka.contrib,
  scalaz.core,
  spray.routing,
  spray.json,
  akka.persistence_cassandra,
  akka.testkit % "test"
)
