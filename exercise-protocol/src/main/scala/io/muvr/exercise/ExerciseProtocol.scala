package io.muvr.exercise

import io.muvr.{UserMessage, UserId}

object ExerciseProtocol {

  /**
   * Submit the entire resistance exercise session
   *
   * @param userId the user identity
   * @param eres the entire session
   */
  case class ExerciseSubmitEntireResistanceExerciseSession(userId: UserId, eres: EntireResistanceExerciseSession) extends UserMessage

}
