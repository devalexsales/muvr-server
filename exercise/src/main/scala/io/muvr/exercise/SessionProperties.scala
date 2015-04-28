package io.muvr.exercise

import java.util.{Date, UUID}


/**
 * The exercise session props
 * @param startDate the start date
 * @param muscleGroups the planned muscle groups
 * @param intendedIntensity the planned intensity
 */
case class SessionProperties(startDate: Date,
                   muscleGroups: Seq[String],
                   intendedIntensity: Double) {
  require(intendedIntensity >  0.0, "intendedIntensity must be between <0, 1)")
  require(intendedIntensity <= 1.0, "intendedIntensity must be between <0, 1)")

  import scala.concurrent.duration._

  /** At brutal (> .95) intensity, we rest for 15-ish seconds */
  private val brutalRest = 15

  /**
   * The duration between sets
   */
  lazy val restDuration: FiniteDuration = (1.0 / intendedIntensity * brutalRest).seconds

}
