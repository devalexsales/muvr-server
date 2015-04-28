package io.muvr

import akka.actor.{Props, Actor, ActorSystem, ActorRef}

trait ProxyScaffolding {
  import ProxyScaffolding._

  def scaffolding(transport: ActorRef, system: ActorSystem): ActorRef = system.actorOf(Props(classOf[ScaffoldingProxy], transport))

}

object ProxyScaffolding {

  class ScaffoldingProxy(transport: ActorRef) extends Actor {
    import Timeouts.defaults._
    import context.dispatcher
    import spray.client.pipelining._

    private val pipeline = sendReceive(transport)

    override def receive: Receive = {
      case x: String ⇒ pipeline(Post("http://muvr-scaffolding.herokuapp.com/exercise/123/444/resistance", x))
    }
  }

}