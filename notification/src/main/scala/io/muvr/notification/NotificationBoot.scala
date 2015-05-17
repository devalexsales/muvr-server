package io.muvr.notification

import akka.actor.{ActorRef, ActorSystem}

/**
 * Booted notification node, with a valid reference to the ``Notification`` actor
 * @param notification the ``Notification`` AR
 */
case class NotificationBoot private(notification: ActorRef)

/**
 * Notification boot
 */
object NotificationBoot {

  /**
   * Boots the notification actors in the given ``system``.
   * @param system the AS that will host the notification actor
   * @return the booted node
   */
  def boot(implicit system: ActorSystem): NotificationBoot = {
    val notification = system.actorOf(Notification.props, Notification.name)
    NotificationBoot(notification)
  }

}
