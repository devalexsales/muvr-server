package io.muvr.exercise

import akka.actor.ActorRef
import io.muvr.CommonMarshallers
import io.muvr.CommonMarshallers.UnmarshalledAndEntity
import spray.http.HttpEntity
import spray.httpx.SprayJsonSupport
import spray.routing.Directives

private[exercise] object ExerciseService extends Directives with SprayJsonSupport with CommonMarshallers {
  import spray.json.DefaultJsonProtocol._

  private implicit val resistanceExerciseFormat = jsonFormat5(ResistanceExercise)
  private implicit val resistanceExerciseSetFormat = jsonFormat1(ResistanceExerciseSet)
  private implicit val resistanceExerciseSetExampleFormat = jsonFormat3(ResistanceExerciseSetExample)

  def route(scaffolding: ActorRef) =
    path("exercise" / Segment / Segment / "resistance") { (x, y) ⇒
      post {
        handleWith { uae: UnmarshalledAndEntity[ResistanceExerciseSetExample] ⇒
          scaffolding ! uae.entity.asString
          println(uae.unmarshalled)
          "{}"
        }
      }
    }

}
