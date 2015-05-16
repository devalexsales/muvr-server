package io.muvr.exercise

/**
 * The rest between exercies
 *
 * @param minimumDuration minimum number of seconds to rest
 * @param maximumDuration maximum number of seconds to rest
 * @param minimumHeartRate the heart rate
 */
case class Rest(minimumDuration: Double, maximumDuration: Double, minimumHeartRate: Int) extends ExercisePlanItem