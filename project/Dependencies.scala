import sbt._
import Keys._

object Dependencies {

  object akka {
    val version = "2.3.10"
    // Core Akka
    val actor                 = "com.typesafe.akka"      %% "akka-actor"                    % version
    val cluster               = "com.typesafe.akka"      %% "akka-cluster"                  % version
    val contrib               = "com.typesafe.akka"      %% "akka-contrib"                  % version intransitive()
    val persistence           = "com.typesafe.akka"      %% "akka-persistence-experimental" % version intransitive()
    val persistence_cassandra = "com.github.krasserm"    %% "akka-persistence-cassandra"    % "0.3.7" intransitive()

    val leveldb               = "org.iq80.leveldb"        % "leveldb"                       % "0.7"

    val testkit               = "com.typesafe.akka"      %% "akka-testkit"                  % version
  }

  object spray {
    val version = "1.3.3"

    val can     = "io.spray" %% "spray-can"                    % version
    val routing = "io.spray" %% "spray-routing-shapeless2"     % version
    val client  = "io.spray" %% "spray-client"                 % version
    val ws      = "com.wandoulabs.akka" %%  "spray-websocket"  % "0.1.4"
    val json    = "io.spray"            %%  "spray-json"       % "1.3.1"

    val testkit = ("io.spray" %% "spray-testkit"            % version)
      .exclude("org.scalamacros", "quasiquotes_2.10.3")
  }

  object scalaz {
    val core = "org.scalaz" %% "scalaz-core" % "7.1.1"
  }

  object slf4j {
    val version = "1.6.1"

    val slf4j_simple     = "org.slf4j"              % "slf4j-simple" % version
    val slf4j_api        = "org.slf4j"              % "slf4j-api"    % version
  }

  val typesafeConfig   = "com.typesafe"           % "config"        % "1.2.1"

  // Apple push notifications
  val apns             = "com.notnoop.apns"       % "apns"         % "0.2.3"
 
  // Datastax Cassandra Client
  val cassandra_driver = "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.5" exclude("io.netty", "netty")

  // Testing
  val scalatest        = "org.scalatest"          %% "scalatest"    % "2.2.4"
  val scalacheck       = "org.scalacheck"         %% "scalacheck"   % "1.12.2"

}
