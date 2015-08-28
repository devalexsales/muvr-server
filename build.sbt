import sbt._
import Keys._

name := "muvr-server"

// Common protocol code
lazy val commonProtocol = project.in(file("common-protocol")).settings(Build.Settings.project)

// Common protocol marshalling
lazy val commonProtocolMarshalling = project.in(file("common-protocol-marshalling")).settings(Build.Settings.project)
  .dependsOn(commonProtocol)

// Common code, but not protocols
lazy val common = project.in(file("common")).settings(Build.Settings.project)
  .dependsOn(commonProtocol)

// Exercise protocol
lazy val exerciseProtocol = project.in(file("exercise-protocol")).settings(Build.Settings.project)
  .dependsOn(common, commonProtocol)

// Exercise protocol marshallers
lazy val exerciseProtocolMarshalling = project.in(file("exercise-protocol-marshalling")).settings(Build.Settings.project)
  .dependsOn(common, commonProtocol, exerciseProtocol, commonProtocolMarshalling)

// Exercise
lazy val exercise = project.in(file("exercise"))
  .settings(Build.Settings.project)
  .dependsOn(notificationProtocol, profileProtocol, exerciseProtocol, exerciseProtocolMarshalling,commonProtocolMarshalling, common)

// User profiles
lazy val profile = project.in(file("profile")).settings(Build.Settings.project)
  .dependsOn(profileProtocol, common, commonProtocolMarshalling)
lazy val profileProtocol = project.in(file("profile-protocol")).settings(Build.Settings.project)
  .dependsOn(common, notificationProtocol)

// Notifications
lazy val notification = project.in(file("notification")).settings(Build.Settings.project)
  .dependsOn(common, notificationProtocol)
lazy val notificationProtocol = project.in(file("notification-protocol")).settings(Build.Settings.project)
  .dependsOn(common)

// Main
lazy val main = project.in(file("main")).settings(Build.Settings.project)
  .dependsOn(exercise, profile, notification, common)

// The main aggregate
lazy val root = (project in file(".")).aggregate(main, exercise, profile, notification, common)

fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

publishLocal := {}

publish := {}
