import Dependencies._

Build.Settings.project

name := "exercise-protocol-marshalling"

libraryDependencies ++= Seq(
  spray.json
)
