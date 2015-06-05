import Dependencies._

Build.Settings.project

name := "common-protocol-marshalling"

libraryDependencies ++= Seq(
  spray.routing,
  spray.json,
  akka.actor
)
