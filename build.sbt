import sbt._
import Keys._

name := "muvr-server"

// Configuration information used to classify tests based on the time they take to run
lazy val LongRunningTest = config("long") extend Test
lazy val ShortRunningTest = config("short") extend Test

// List of tests that require extra running time (used by CI to stage testing runs)
val longRunningTests = Seq(
  "io.muvr.exercise.SomeTest"
)

// Common protocol code
lazy val commonProtocol = project.in(file("common-protocol"))

// Common code, but not protocols
lazy val common = project.in(file("common")).dependsOn(commonProtocol)

// Exercise protocol 
lazy val exerciseProtocol = project.in(file("exercise-protocol")).dependsOn(common, commonProtocol)

// Exercise protocol marshallers
lazy val exerciseProtocolMarshalling = project.in(file("exercise-protocol-marshalling")).dependsOn(common, commonProtocol, exerciseProtocol)

// Exercise
lazy val exercise = project.in(file("exercise"))
  .dependsOn(notificationProtocol, profileProtocol, exerciseProtocol, exerciseProtocolMarshalling, common)
  .configs(LongRunningTest, ShortRunningTest)
  .settings(inConfig(LongRunningTest)(Defaults.testTasks): _*)
  .settings(inConfig(ShortRunningTest)(Defaults.testTasks): _*)
  .settings(
    testOptions in LongRunningTest := Seq(Tests.Filter(longRunningTests.contains)),
    testOptions in ShortRunningTest := Seq(Tests.Filter((name: String) => !longRunningTests.contains(name)))
  )

// User profiles
lazy val profile = project.in(file("profile")).dependsOn(profileProtocol, common)
lazy val profileProtocol = project.in(file("profile-protocol")).dependsOn(common, notificationProtocol)

// Notifications
lazy val notification = project.in(file("notification")).dependsOn(common, notificationProtocol)
lazy val notificationProtocol = project.in(file("notification-protocol")).dependsOn(common)

// Main
lazy val main = project.in(file("main")).dependsOn(exercise, profile, notification, common)

// The main aggregate
lazy val root = (project in file(".")).aggregate(main, exercise, profile, notification, common)

fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

publishLocal := {}

publish := {}
