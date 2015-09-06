package io.muvr.exercise

import java.util.Date

/**
 * The resistance exercise session
 *
 * @param startDate the start date and time
 * @param exerciseModel the exercise model
 * @param intendedIntensity the targeted intensity
 * @param title the optional title
 */
case class ResistanceExerciseSession(startDate: Date, exerciseModel: ExerciseModel,
                                     intendedIntensity: Double,
                                      title: String)

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
 * @param examples the classification examples
 */
case class EntireResistanceExerciseSession(id: SessionId,
                                            session: ResistanceExerciseSession,
                                            examples: List[ResistanceExerciseExample])
