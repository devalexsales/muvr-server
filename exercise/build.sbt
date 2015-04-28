import Dependencies._

Build.Settings.project

name := "exercise"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.persistence,
  akka.streams.core,
  scalaz.core,
  // Apple push notifications
  apns,
  slf4j.slf4j_simple,
  // Testing
  scalatest % "test",
  scalacheck % "test"
)
