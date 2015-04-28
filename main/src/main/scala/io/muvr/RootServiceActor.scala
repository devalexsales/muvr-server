package io.muvr

import akka.actor.Props
import spray.routing.{RouteConcatenation, HttpServiceActor, Route}

object RootServiceActor extends RouteConcatenation {
  def props(routes: Seq[Route]): Props = Props(classOf[RootServiceActor], routes.reduce(_ ~ _))
}

class RootServiceActor(route: Route) extends HttpServiceActor {
  override def receive: Receive = runRoute(route)
}
