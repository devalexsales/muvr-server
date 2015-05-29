package io.muvr.profile

import akka.actor.{ActorLogging, Props}
import akka.contrib.pattern.ShardRegion
import akka.persistence.{PersistentActor, SnapshotOffer}
import io.muvr.{UserMessage, AutoPassivation, UserId}
import io.muvr.notification.NotificationProtocol.{Device, Devices}

object UserProfile {
  import io.muvr.profile.UserProfileProtocol._
  val shardName = "user-profile"
  val props = Props[UserProfile]
  
  val idExtractor: ShardRegion.IdExtractor = {
    case UserRegistered(userId, account)       ⇒ (userId.toString, account)
    case UserGetAccount(userId)                ⇒ (userId.toString, GetAccount)
    case UserGetPublicProfile(userId)          ⇒ (userId.toString, GetPublicProfile)
    case UserPublicProfileSet(userId, profile) ⇒ (userId.toString, profile)
    case UserGetDevices(userId)                ⇒ (userId.toString, GetDevices)
    case UserDeviceSet(userId, device)         ⇒ (userId.toString, DeviceSet(device))
    case UserGetProfileImage(userId)           ⇒ (userId.toString, GetProfileImage)
    case UserProfileImageSet(userId, image)    ⇒ (userId.toString, image)
  }

  val shardResolver: ShardRegion.ShardResolver = {
    case x: UserMessage ⇒ x.shardRegion()
  }

  /**
   * Sets the user's device
   * @param device the device
   */
  case class DeviceSet(device: Device)

  /**
   * Get account
   */
  private case object GetAccount

  /**
   * Get public profile
   */
  private case object GetPublicProfile

  /**
   * Get profile image
   */
  private case object GetProfileImage

  /**
   * Get all devices
   */
  private case object GetDevices
}

/**
 * User profile domain
 */
class UserProfile extends PersistentActor with ActorLogging with AutoPassivation {
  import io.muvr.profile.UserProfile._
  import io.muvr.profile.UserProfileProtocol._
  import scala.concurrent.duration._

  private var profile: Profile = _

  override def persistenceId: String = s"user-profile-${self.path.name}"

  // the shard lives for the specified timeout seconds before passivating
  context.setReceiveTimeout(360.seconds)

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, offeredSnapshot: Profile) ⇒
      log.debug("SnapshotOffer: not registered -> registered.")
      profile = offeredSnapshot
      context.become(registered)
    case acc: Account ⇒ profile = Profile(acc, Devices.empty, None, None)
    case pp: PublicProfile ⇒ profile = profile.withPublicProfile(pp)
    case pi: Array[Byte] ⇒ profile = profile.withProfileImage(pi)
  }

  override def receiveCommand: Receive = notRegistered

  private def notRegistered: Receive = withPassivation {
    case cmd: Account ⇒
      persist(cmd) { acc ⇒
        profile = Profile(acc, Devices.empty, None, None)
        saveSnapshot(profile)
        context.become(registered)
      }
  }

  private def registered: Receive = withPassivation {
    case ds: DeviceSet ⇒ persist(ds) { evt ⇒
      profile = profile.withDevice(evt.device)
    }
    case pp: PublicProfile ⇒ persist(pp) { evt ⇒
      profile = profile.withPublicProfile(evt)
    }
    case pi: Array[Byte] ⇒ persist(pi) { evt ⇒
      profile = profile.withProfileImage(evt)
    }

    case GetPublicProfile ⇒
      sender() ! profile.publicProfile
    case GetAccount ⇒
      sender() ! profile.account
    case GetDevices ⇒
      sender() ! profile.devices
    case GetProfileImage ⇒
      sender() ! profile.profileImage
  }

}
