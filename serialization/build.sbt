import Dependencies._

Build.Settings.project

name := "serialization"

libraryDependencies ++= Seq(
  akka.actor % "provided",
  akka.persistence % "provided"
)
