package io.muvr

import collection.JavaConversions._
import akka.actor._
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import com.typesafe.config.ConfigFactory

/**
 * CLI application for the exercise app
 */
object MuvrApp extends App with Muvr with ProxyScaffolding {
  private var store: Option[ActorRef] = None

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
