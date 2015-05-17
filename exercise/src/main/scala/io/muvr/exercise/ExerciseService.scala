package io.muvr.exercise

import akka.actor.ActorRef
import io.muvr.CommonMarshallers.UnmarshalledAndEntity
import io.muvr.exercise.ExerciseProtocol.ExerciseSubmitEntireResistanceExerciseSession
import io.muvr.{CommonMarshallers, CommonPathDirectives}
import spray.httpx.SprayJsonSupport
import spray.json._
import spray.routing._

import scala.concurrent.ExecutionContext

/**
 * REST API for the exercise service. Exposes endpoints that allow the clients to submit entire exercise sessions, to
 * submit exercise examples
 */
private[exercise] object ExerciseService extends Directives with SprayJsonSupport with CommonMarshallers with CommonPathDirectives {
  import spray.json.DefaultJsonProtocol._
  private val SessionIdValue: PathMatcher1[SessionId] = JavaUUID.map(SessionId.apply)
  private implicit object SessionIdFormat extends RootJsonFormat[SessionId] {
    override def write(obj: SessionId): JsValue = JsString(obj.toString)
    override def read(json: JsValue): SessionId = (json: @unchecked) match {
      case JsString(x) ⇒ SessionId(x)
    }
  }
  private implicit val resistanceExerciseSessionFormat = jsonFormat4(ResistanceExerciseSession)
  private implicit val resistanceExerciseFormat = jsonFormat5(ResistanceExercise)
  private implicit val resistanceExerciseSetFormat = jsonFormat1(ResistanceExerciseSet)
  private implicit val resistanceExerciseSetExampleFormat = jsonFormat3(ResistanceExerciseSetExample)
  private implicit object ExercisePlanFormat extends JsonFormat[ExercisePlanItem] {
    private val restFormat = jsonFormat3(io.muvr.exercise.Rest)
    override def read(json: JsValue): ExercisePlanItem = json.asJsObject.getFields("kind", "value") match {
      case Seq(JsString("rest"), rest) ⇒ restFormat.read(rest)
      case Seq(JsString("resistance-exercise"), resistanceExercise) ⇒ resistanceExerciseFormat.read(resistanceExercise)
      case x ⇒ throw new DeserializationException("Bad kind " + x)
    }
    override def write(obj: ExercisePlanItem): JsValue = obj match {
      case r: Rest ⇒ restFormat.write(r)
      case e: ResistanceExercise ⇒ resistanceExerciseFormat.write(e)
      case x ⇒ throw new SerializationException("Unknown type " + x.getClass)
    }
  }
  private implicit val exercisePlanDeviation = jsonFormat2(ExercisePlanDeviation)
  private implicit val entireResistanceExerciseSessionFormat = jsonFormat5(EntireResistanceExerciseSession)

  import akka.pattern.ask
  import io.muvr.Timeouts.defaults._

  def route(exercise: ActorRef, scaffolding: ActorRef)(implicit ec: ExecutionContext) =
    path("exercise" / UserIdValue / "resistance" / "example") { (_) ⇒
      post {
        handleWith { uae: UnmarshalledAndEntity[ResistanceExerciseSetExample] ⇒
          scaffolding ! uae.entity.asString
          println(uae.unmarshalled)
        }
      }
    } ~
    path("exercise" / UserIdValue / "resistance") { userId ⇒
      post {
        handleWith { eres: EntireResistanceExerciseSession ⇒
          (exercise ? ExerciseSubmitEntireResistanceExerciseSession(userId, eres)).mapRight[SessionId]
        }
      }
    }

}
