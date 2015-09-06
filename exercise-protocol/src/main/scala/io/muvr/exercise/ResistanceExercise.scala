package io.muvr.exercise

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
 * Sensor data ADT
 */
sealed trait SensorData

/**
 * Three dimensional value
 *
 * @param x the x
 * @param y the y
 * @param z the z
 */
case class Threed(x: Int, y: Int, z: Int) extends SensorData

/**
 * One dimensional value
 * @param value the value
 */
case class Oned(value: Int) extends SensorData

/**
 * Fused sensor data format from the given device recording at sampling rate
 *
 * @param deviceId the device identity
 * @param samplesPerSecond the samples per second
 * @param sensorType the sensor type
 * @param sensorLocation the sensor location
 * @param data the sensory data
 */
case class FusedSensorData(deviceId: Int, samplesPerSecond: Int, sensorType: Int, sensorLocation: Int, data: Seq[SensorData])

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
case class ResistanceExerciseExample(classified: List[ResistanceExercise],
                                        correct: Option[ResistanceExercise],
                                        fusedSensorData: List[FusedSensorData])
