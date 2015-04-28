package io.muvr

import akka.actor.Props
import spray.routing.{HttpServiceActor, Route}

object RootServiceActor {
  def props(route: Route): Props = Props(classOf[RootServiceActor], route)
}

class RootServiceActor(route: Route) extends HttpServiceActor {
  override def receive: Receive = runRoute(route)
}
