package io.muvr.exercise

import io.muvr.UserId

case class UserExerciseProcessorPersistenceId(userId: UserId) {

  def persistenceId: String = s"user-exercises-${userId.toString}"

}

object UserExerciseProcessorPersistenceId {
  private val pattern = "user-exercises-([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})".r

  def unapply(persistenceId: String): Option[UserId] = persistenceId match {
    case pattern(userId) ⇒ Some(UserId(userId))
    case _ ⇒ None
  }

}
