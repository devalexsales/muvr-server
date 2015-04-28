package io.muvr.notification

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.RoundRobinPool

/**
 * The notification delivery actor. It can deliver the ``PushMessagePayload`` values to the given devices.
 */
object Notification {
  val name = "notification"
  val props = Props(classOf[Notification]).withRouter(RoundRobinPool(nrOfInstances = 15))
}

/**
 * The notification delivery actor
 */
class Notification extends Actor with ActorLogging {
  import io.muvr.notification.NotificationProtocol._
  private val apple = context.actorOf(ApplePushNotification.props)

  override def receive: Receive = {
    case PushMessage(devices, payload) ⇒
      devices.foreach {
        case IOSDevice(deviceToken) ⇒ apple ! ApplePushNotification.PushMessage(deviceToken, payload)
        case AndroidDevice() ⇒ log.debug(s"Not yet delivering Android push message $payload")
      }
  }
}
