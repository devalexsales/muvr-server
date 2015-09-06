package io.muvr.exercise

/**
 * The exercise model
 *
 * @param id the identity
 * @param title the name
 * @param exercises the exercises this model handles
 */
case class ExerciseModel(id: String, title: String, exercises: List[String])