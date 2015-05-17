package io.muvr.exercise

import akka.actor.{Actor, ActorLogging}
import akka.persistence.PersistentActor
import io.muvr.UserId

class UserExerciseProcessor extends PersistentActor with ActorLogging {
  // user reference and notifier
  private val userId = UserId(self.path.name)

  // per-user actor
  override val persistenceId: String = s"user-exercises-${userId.toString}"

  override def receiveRecover: Receive = Actor.emptyBehavior

  override def receiveCommand: Receive = ???

}
