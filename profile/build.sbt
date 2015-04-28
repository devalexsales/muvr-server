import Dependencies._

Build.Settings.project

name := "profile"

libraryDependencies ++= Seq(
  akka.actor,
  akka.cluster,
  akka.contrib,
  scalaz.core,
  akka.persistence_cassandra,
  akka.testkit % "test"
)
