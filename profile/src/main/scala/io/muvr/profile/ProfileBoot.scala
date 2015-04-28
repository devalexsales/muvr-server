package io.muvr.profile

import akka.actor.{ActorSystem, ActorRef}
import akka.contrib.pattern.ClusterSharding

import scala.concurrent.ExecutionContext

case class ProfileBoot(userProfile: ActorRef, private val userProfileProcessor: ActorRef) {
  def route(ec: ExecutionContext) = ProfileService.userProfileRoute(userProfile, userProfileProcessor)(ec)
}

object ProfileBoot {

  def boot(implicit system: ActorSystem): ProfileBoot = {
    val userProfile = ClusterSharding(system).start(
      typeName = UserProfile.shardName,
      entryProps = Some(UserProfile.props),
      idExtractor = UserProfile.idExtractor,
      shardResolver = UserProfile.shardResolver)
    val userProfileProcessor = system.actorOf(UserProfileProcessor.props(userProfile), UserProfileProcessor.name)

    ProfileBoot(userProfile, userProfileProcessor)
  }

}
