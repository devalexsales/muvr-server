import Dependencies._

Build.Settings.project

name := "main"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.persistence,
  akka.persistence_cassandra,
  akka.leveldb,
  // Codec
  scalaz.core,
  // Spray
  spray.routing,
  spray.can,
  spray.client,
  spray.json,
  spray.ws,
  // Apple push notifications
  apns,
  slf4j.slf4j_simple,
  // Testing
  scalatest % "test",
  scalacheck % "test",
  akka.testkit % "test"
)
/*
import DockerKeys._
import sbtdocker.ImageName
import sbtdocker.mutable.Dockerfile

dockerSettings

// Define a Dockerfile for:
//   - `LiftLocalMonolithApp`
//   - `LiftMonolithApp`

mainClass in assembly := Some("com.eigengo.lift.LiftMonolithApp")

docker <<= (docker dependsOn assembly)

dockerfile in docker := {
  val artifact = (outputPath in assembly).value
  val artifactTargetPath = s"/app/${artifact.name}"
  val debTargetPath = "/app/debs"
  new Dockerfile {
    from("dockerfile/java")
    val f = new File(s"${Path.userHome.absolutePath}/.ios")
    if (f.exists) add(f, "/root/.ios")
    add(artifact, artifactTargetPath)
    val d = new File(s"${sourceDirectory.value}/../../debs")
    if (d.exists) add(d, debTargetPath)
    run("apt-get", "update")
    // Install CVC4, JNI shared library/bindings - used by exercise classification models
    //
    // NOTE: Debian packages (i.e. `cvc4` and `libcvc4bindings-java3`) in the `debs` directory are (currently)
    // sourced from:
    //   deb http://cvc4.cs.nyu.edu/debian unstable/
    run("apt-get", "install", "-y", "--force-yes", "libantlr3c-3.2-0")
    run("dpkg", "-R", "-i", debTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

imageName in docker := {
  ImageName(
    namespace = Some("janm399"),
    repository = "lift",
    tag = Some(s"${name.value}-production"))
}
*/