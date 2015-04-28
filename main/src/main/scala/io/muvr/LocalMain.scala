package io.muvr

import akka.actor.ActorSystem
import akka.io.IO
import io.muvr.exercise.ExerciseService
import spray.can.Http
import spray.can.server.UHttp

object LocalMain extends App {
  implicit lazy val system = ActorSystem("reactive-system")
  sys.addShutdownHook({ system.shutdown() })
  
  val scaffolding = system.actorOf(WebSocketServiceActor.props)
  // val exerciseProcessor = system.actorOf(ExerciseProcessor.props)
  // val sessionsView = system.actorOf(SessionsView.props)
  // ...

  val combinedRoute = ExerciseService.route(scaffolding)
  
  // construct the main API
  val api = system.actorOf(RootServiceActor.props(combinedRoute))
  IO(UHttp) ! Http.Bind(api, Configuration.host, Configuration.portHttp)

  // as it happens, our scaffolding is also an API
  IO(UHttp) ! Http.Bind(scaffolding, Configuration.host, Configuration.portWs)
}

object Configuration {
  import com.typesafe.config.ConfigFactory

  private val config = ConfigFactory.load
  config.checkValid(ConfigFactory.defaultReference)

  lazy val host = ""
  lazy val portHttp = 8080
  lazy val portWs = 8081
}
