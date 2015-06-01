package io.muvr.exercise

import io.muvr.{CommonPathDirectives, CommonMarshallers}
import spray.httpx.SprayJsonSupport
import spray.json._
import spray.routing._

trait ExerciseServiceMarshallers extends SprayJsonSupport with CommonMarshallers with CommonPathDirectives {
  import spray.json.DefaultJsonProtocol._
  val SessionIdValue: PathMatcher1[SessionId] = JavaUUID.map(SessionId.apply)
  implicit object SessionIdFormat extends RootJsonFormat[SessionId] {
    override def write(obj: SessionId): JsValue = JsString(obj.toString)
    override def read(json: JsValue): SessionId = (json: @unchecked) match {
      case JsString(x) ⇒ SessionId(x)
    }
  }
  implicit val resistanceExerciseSessionFormat = jsonFormat4(ResistanceExerciseSession)
  implicit val resistanceExerciseFormat = jsonFormat5(ResistanceExercise)
  implicit val resistanceExerciseSetFormat = jsonFormat1(ResistanceExerciseSet)
  implicit val resistanceExerciseSetExampleFormat = jsonFormat3(ResistanceExerciseSetExample)
  implicit object ExercisePlanFormat extends JsonFormat[ExercisePlanItem] {
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
  implicit val exercisePlanDeviation = jsonFormat2(ExercisePlanDeviation)
  implicit val entireResistanceExerciseSessionFormat = jsonFormat5(EntireResistanceExerciseSession)

}
