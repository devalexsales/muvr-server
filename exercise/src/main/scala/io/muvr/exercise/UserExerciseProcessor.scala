package io.muvr.exercise

import akka.actor.{Props, Actor, ActorLogging}
import akka.contrib.pattern.{ShardRegion, ClusterSharding}
import akka.persistence.PersistentActor
import io.muvr.exercise.ExerciseProtocol.ExerciseSubmitEntireResistanceExerciseSession
import io.muvr.{UserMessage, UserId}

import scalaz.\/

/**
 * Companion for the ``UserExerciseProcessor`` actor. Defines the fields and messages.
 */
object UserExerciseProcessor {
  val shardName = "user-exercise-processor"
  val props: Props = Props[UserExerciseProcessor]

  val idExtractor: ShardRegion.IdExtractor = {
    case ExerciseSubmitEntireResistanceExerciseSession(userId, eres) ⇒ (userId.toString, eres)
  }

  val shardResolver: ShardRegion.ShardResolver = {
    case x: UserMessage ⇒ x.shardRegion()
  }

}

/**
 * The cluster-sharded persistent actor that processes the user messages
 */
class UserExerciseProcessor extends PersistentActor with ActorLogging {
  // user reference and notifier
  private val userId = UserId(self.path.name)

  // per-user actor
  override val persistenceId: String = s"user-exercises-${userId.toString}"

  // no recovery behaviour just yet
  override def receiveRecover: Receive = Actor.emptyBehavior

  // only deal with sessions
  override def receiveCommand: Receive = {
    case eres@EntireResistanceExerciseSession(id, session, sets, examples, deviations) ⇒
      persist(eres) { _ ⇒
        sender() ! \/.right(id)
      }
  }

}
