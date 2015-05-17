package io.muvr.exercise

import java.util.Date


/**
 * The exercise session props
 * @param muscleGroupIds the planned muscle groups
 * @param intendedIntensity the planned intensity
 */
case class ResistanceExerciseSessionProperties(muscleGroupIds: Seq[String],
                                               intendedIntensity: Double) {
  require(intendedIntensity >= 0.0, "intendedIntensity must be between (0, 1)")
  require(intendedIntensity <= 1.0, "intendedIntensity must be between (0, 1)")

  import scala.concurrent.duration._

  /** At brutal (> .95) intensity, we rest for 15-ish seconds */
  private val brutalRest = 15

  /**
   * The duration between sets
   */
  lazy val restDuration: FiniteDuration = (1.0 / intendedIntensity * brutalRest).seconds
  
}

/**
 * The resistance exercise session
 *
 * @param startDate the start date and time
 * @param properties the session properties
 */
case class ResistanceExerciseSession(startDate: Date, properties: ResistanceExerciseSessionProperties)

/**
 * Holds the entire resistance exercise session. This message contains all information about an
 * exercise session. The most common scenario is that the app submits the entire session in one
 * request, it is possible to combine multiple requests to slowly build-up the picture of the
 * running session.
 *
 * The client generates the session identity, and the server should accept it.
 *
 * @param id the session identity established on the mobile.
 * @param session the session descriptor
 * @param sets the completed exercise sets
 * @param examples the classification examples
 * @param deviations the deviations from the exercise plan
 */
case class EntireResistanceExerciseSession(id: SessionId,
                                            session: ResistanceExerciseSession,
                                            sets: List[ResistanceExerciseSet],
                                            examples: List[ResistanceExerciseSetExample],
                                            deviations: List[ExercisePlanDeviation])
