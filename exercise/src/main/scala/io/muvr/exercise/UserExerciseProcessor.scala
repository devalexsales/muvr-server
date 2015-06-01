package io.muvr.exercise

import akka.actor.{ActorLogging, Props}
import akka.contrib.pattern.ShardRegion
import akka.persistence.PersistentActor
import io.muvr.{UserId, UserMessage}

import scalaz.\/

/**
 * Companion for the ``UserExerciseProcessor`` actor. Defines the fields and messages.
 */
object UserExerciseProcessor {
  val shardName = "user-exercise-processor"
  val props: Props = Props[UserExerciseProcessor]

  val idExtractor: ShardRegion.IdExtractor = {
    case ExerciseSubmitEntireResistanceExerciseSession(userId, eres) ⇒ (userId.toString, eres)
    case Foo(userId)                                                 ⇒ (userId.toString, 'foo)
  }

  val shardResolver: ShardRegion.ShardResolver = {
    case x: UserMessage ⇒ x.shardRegion()
  }

  /**
   * Submit the entire resistance exercise session
   *
   * @param userId the user identity
   * @param eres the entire session
   */
  case class ExerciseSubmitEntireResistanceExerciseSession(userId: UserId, eres: EntireResistanceExerciseSession) extends UserMessage

  case class Foo(userId: UserId) extends UserMessage
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
  override def receiveRecover: Receive = {
    case r: Rest ⇒ println(r)
  }

  // only deal with sessions
  override def receiveCommand: Receive = {
    case eres@EntireResistanceExerciseSession(id, session, sets, examples, deviations) ⇒
      persist(eres) { _ ⇒
        sender() ! \/.right(id)
      }
    case 'foo ⇒
      persist(Rest(10, 20, 100)) { _ ⇒ }
  }

}
