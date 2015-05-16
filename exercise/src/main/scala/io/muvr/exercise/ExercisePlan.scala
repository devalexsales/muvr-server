package io.muvr.exercise

/**
 * Marker trait for all members of exercise plan
 */
trait ExercisePlanItem

/**
 * The deviation from the plan
 *
 * @param planned the planned item
 * @param actual the actual item
 */
case class ExercisePlanDeviation(planned: ExercisePlanItem, actual: ExercisePlanItem)
