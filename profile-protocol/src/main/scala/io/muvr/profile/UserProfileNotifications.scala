package io.muvr.profile

import akka.actor.{Props, Actor, ActorRef}
import io.muvr.UserId
import io.muvr.notification.NotificationProtocol.{Devices, PushMessage, PushMessagePayload}
import io.muvr.profile.UserProfileProtocol.UserGetDevices

/**
 * Convenience trait that provides type that can send notification to all user devices
 */
trait UserProfileNotifications {
  this: Actor ⇒
  import UserProfileNotifications._

  def newNotificationSender(userId: UserId, notification: ActorRef, userProfile: ActorRef): ActorRef =
    context.actorOf(Props(classOf[UserProfileNotificationsSender], userId, notification, userProfile))
  
}

object UserProfileNotifications {

  /**
   * Convenience sender
   * @param userId the user identity
   * @param notification the notification actor
   * @param userProfile the user profile actor
   */
  private class UserProfileNotificationsSender(userId: UserId, notification: ActorRef, userProfile: ActorRef) extends Actor {
    import scala.concurrent.duration._
    private var devices: Option[Devices] = None

    userProfile ! UserGetDevices(userId)

    override def receive: Receive = {
      case ds: Devices ⇒ devices = Some(ds)
      case payload: PushMessagePayload if devices.isEmpty ⇒
        userProfile ! UserGetDevices(userId)
        context.system.scheduler.scheduleOnce(1.second, self, payload)(context.dispatcher)
      case payload: PushMessagePayload if devices.isDefined ⇒
        notification ! PushMessage(devices.get.justLast, payload)
    }
  }

}

