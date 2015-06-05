package io.muvr.tools

import java.util.Date

import akka.actor.ActorSystem
import io.muvr.UserId
import io.muvr.exercise._
import io.muvr.profile.ProfileMarshallers
import io.muvr.profile.UserProfileProcessor.UserRegister

import scala.concurrent.{Await, Future}
import scala.util.Random

object ERESGenerator extends ExerciseProtocolMarshallers with ProfileMarshallers {

  private val defaultExercises = List(
    "arms" → List("dumbbell-bicep-curl", "straight-bar-biceps-curl", "rope-triceps-extension", "rope-biceps-curl", "alt-dumbbell-biceps-curl", "triceps-dips", "barbell-biceps-curl"),
    "chest" → List("dumbbell-chest-press", "dumbbell-chest-fly", "angle-chest-press", "cable-cross-overs"),
    "core" → List("side-dips", "twist", "pulldown-crunch"),
    "back" → List("cable-deltoid-cross-overs", "deltoid-row", "leverage-high-row", "lat-pulldown", "dumbbell-row"),
    "shoulders" → List("dumbbell-press", "dumbbell-side-rise", "barbell-press", "dumbbell-front-rise")
  )

  private val defaultWeights = Map(
    "arms" → 25,
    "chest" → 25,
    "core" → 25,
    "back" → 25,
    "shoulders" → 15
  )

  private def repetitionsGenerator(muscleGroupId: String)(intensity: Double, exercise: String): Int = {
    (10 + (0.5 - intensity * 4)).toInt
  }

  private def weightGenerator(muscleGroupId: String)(intensity: Double, exercise: String): Option[Double] = {
    defaultWeights.get(muscleGroupId).map { w ⇒ w + (0.5 - intensity * w) }
  }

  private def intensityGenerator(muscleGroupId: String, intendedIntensity: Double): Double = {
    intendedIntensity + math.random * intendedIntensity / 5
  }

  private implicit class RichList[A](list: List[A]) {

    def random: A = {
      val i = Random.nextInt(list.size - 1)
      list(i)
    }
  }

  private def generate(count: Int, each: ⇒ Int): List[EntireResistanceExerciseSession] = {
    List.fill(count) {
      val (muscleGroupId, exercises) = defaultExercises.random
      val intendedIntensity = Random.nextDouble()
      val sets = List.fill(each) {
        val exercise = exercises.random

        val intensity = intensityGenerator(muscleGroupId, intendedIntensity)
        val repetitions = repetitionsGenerator(muscleGroupId)(intensity, exercise)
        val weight = weightGenerator(muscleGroupId)(intensity, exercise)

        ResistanceExerciseSet(List(ResistanceExercise(exercise, 1, Some(repetitions), weight, Some(intensity))))
      }

      val session = ResistanceExerciseSession(new Date(), Seq(muscleGroupId), intendedIntensity, "Generated")
      EntireResistanceExerciseSession(SessionId.randomId(), session, sets, Nil, Nil)
    }
  }

  def main(args: Array[String]): Unit = {
    import spray.client.pipelining._
    import spray.http._

    import scala.concurrent.duration._

    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val userRegistrationPipeline = pipeline ~> unmarshal[UserId]
    val userId = Await.result(userRegistrationPipeline(Post("http://localhost:12551/user", UserRegister(Random.nextString(20), "letmein"))), 10.seconds)
    val count = 100

    generate(count, Random.nextInt(10) + 5).foreach { x ⇒
      val json = entireResistanceExerciseSessionFormat.write(x)
      println(json.prettyPrint)

      val res = Await.result(pipeline(Post(s"http://localhost:12551/exercise/$userId/resistance", x)), 10.seconds)
      println(res)
    }

    println(s"** Registered $userId and submitted $count sessions.")
  }

}
