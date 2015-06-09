package io.muvr

import akka.actor._
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._

/**
 * CLI application for the exercise app
 */
object MuvrApp extends App with Muvr with ProxyScaffolding {

  lazy val config = {
    val role = "muvr-monolith"
    val clusterShardingConfig = ConfigFactory.parseString(s"akka.contrib.cluster.sharding.role=$role")
    val clusterRoleConfig = ConfigFactory.parseString(s"akka.cluster.roles=[$role]")

    clusterShardingConfig
      .withFallback(clusterRoleConfig)
      .withFallback(ConfigFactory.load("main.conf"))
  }

  override def journalStartUp(system: ActorSystem): Unit = ()

  val ports = config.getIntList("akka.cluster.jvm-ports")
  ports.foreach(port ⇒ actorSystemStartUp(port, 10000 + port))
}
