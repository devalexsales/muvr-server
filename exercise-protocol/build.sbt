import Dependencies._

Build.Settings.project

name := "exercise-protocol"

libraryDependencies ++= Seq(
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.testkit % "test"
)
