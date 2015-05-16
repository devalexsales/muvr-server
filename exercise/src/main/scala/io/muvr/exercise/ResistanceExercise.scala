package io.muvr.exercise

import java.util.Date

import spray.json.JsValue

/**
 * Resistance exercise models a typical exercise a user performs against some weight.
 * At the very least, an RE holds some language-independent identifier of the exercise
 * being performed (i.e. 'arms/bicep-curl'). This model does not enforce it, but it is
 * a good idea for the ``exercise`` to uniquely refer to an item in some taxonomy of
 * exercises, which will enable much better analysis.
 *
 * @param exercise the exercise identifier
 * @param confidence the classification confidence
 * @param repetitions the number of repetitions
 * @param weight the weight being used
 * @param intensity the intensity
 */
case class ResistanceExercise(exercise: String, confidence: Double,
                              repetitions: Option[Int], weight: Option[Double],
                              intensity: Option[Double]) extends ExercisePlanItem

/**
 * Exercise set contains several "sets" of exercise. In the simplest case,
 * the exercise set contains exactly one set with exactly one exercise.
 *
 * This is the simplest case of user picking up a dumbbell and doing 10 bicep curls,
 * for example.
 *
 * A more complex scenario is, for example, a drop-set, where the user performs the
 * same exercise, but drops the weight while maintaining the intensity. Such a drop-set
 * might contain, for example, RE(bicep curl, 20 kg), RE(bicep curl, 15 kg), RE(bicep curl, 10 kg).
 *
 * It is also possible to have a super-set, where the user combines different exercises, perhaps
 * exercising all the way until failure. So, we could have RE(bicep curl), RE(tricep press),
 * RE(bicep curl), RE(tricep press).
 *
 * @param sets the sets
 */
case class ResistanceExerciseSet(sets: List[ResistanceExercise])

/**
 * Resistance exercise set example that the app submits to provide positive or negative
 * examples. We say that the system "got it right" if ``Some(classified.head) == correct``.
 * Naturally, if ``classified`` is empty, then the app got it wrong; if ``correct`` is
 * defined, then the user has not exercised, but the app has classified the non-exercise
 * as some exercise.
 *
 * @param classified the result of the classification
 * @param correct the correct classification
 * @param fusedSensorData the exported data used for the classification
 */
case class ResistanceExerciseSetExample(classified: List[ResistanceExerciseSet],
                                        correct: Option[ResistanceExerciseSet],
                                        fusedSensorData: JsValue)
