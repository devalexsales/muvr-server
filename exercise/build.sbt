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
  // For REST API
  spray.httpx,
  spray.can,
  spray.routing,
  // Json
  json4s.native,
  json4s.jackson,
  scalaz.core,
  // Apple push notifications
  apns,
  slf4j.slf4j_simple,
  // Testing
  scalatest % "test",
  scalacheck % "test",
  akka.testkit % "test",
  spray.testkit % "test",
  akka.streams.testkit % "test"
)
