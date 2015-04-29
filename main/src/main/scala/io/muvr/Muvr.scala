package io.muvr

import akka.actor.{ActorRef, ActorSystem}
import akka.io.IO
import com.typesafe.config.{Config, ConfigFactory}
import io.muvr.exercise.ExerciseBoot
import io.muvr.notification.NotificationBoot
import io.muvr.profile.ProfileBoot
import spray.can.Http
import spray.can.server.UHttp
import spray.routing.Route

/**
 * Defines the lift monolith that constructs all services that make up the Lift
 * application.
 *
 * Subclasses must provide ``config`` and implement ``journalStartUp`` in the
 * appropriate fashion.
 */
trait Muvr {

  /**
   * Implementations must return the entire ActorSystem configuraiton
   * @return the configuration
   */
  def config: Config

  /**
   * Implementations must return the ActorRef that will handle the scaffolding / monitoring
   * @return the scaffolding AR
   */
  def scaffolding(transport: ActorRef, system: ActorSystem): ActorRef

  /**
   * Implementations can perform any logic required to start or join a journal
   * @param system the ActorSystem that needs the journal starting or looking up
   */
  def journalStartUp(system: ActorSystem): Unit

  /**
   * Starts up the Lift ActorSystem, binding Akka remoting to ``port`` and exposing all
   * rest services at ``0.0.0.0:restPort``.
   * @param port the Akka port
   * @param restPort the REST services port
   */
  final def actorSystemStartUp(port: Int, restPort: Int): Unit = {
    // Create an Akka system
    val finalConfig =
      ConfigFactory.parseString(
        s"""
           |akka.remote.netty.tcp.port=$port
         """.stripMargin).
      withFallback(config)

    implicit val system = ActorSystem("Muvr", finalConfig)
    import system.dispatcher

    val transport = IO(UHttp)

    // Startup the journal - typically this is only used when running locally with a levelDB journal
    journalStartUp(system)

    // boot the microservices
    val scaff = scaffolding(transport, system)
    val profile = ProfileBoot.boot
    val notification = NotificationBoot.boot
    val exercise = ExerciseBoot.boot(notification.notification, profile.userProfile)

    startupHttpService(transport, restPort, exercise.route(scaff), profile.route)
  }

  /**
   * Startup the REST API handler
   * @param system the (booted) ActorSystem
   * @param port the port
   * @param routes the routes
   */
  private def startupHttpService(transport: ActorRef, port: Int, routes: Route*)(implicit system: ActorSystem): Unit = {
    val api = system.actorOf(RootServiceActor.props(routes))
    transport ! Http.Bind(api, interface = "0.0.0.0", port = port)
  }

}
