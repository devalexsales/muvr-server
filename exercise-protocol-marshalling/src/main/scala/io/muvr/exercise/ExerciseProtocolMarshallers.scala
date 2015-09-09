package io.muvr.exercise

import io.muvr.{CommonPathDirectives, CommonProtocolMarshallers}
import spray.httpx.SprayJsonSupport
import spray.json._
import spray.routing._

trait ExerciseProtocolMarshallers extends SprayJsonSupport with CommonProtocolMarshallers with CommonPathDirectives {
  import spray.json.DefaultJsonProtocol._

  val SessionIdValue: PathMatcher1[SessionId] = JavaUUID.map(SessionId.apply)
  implicit object SessionIdFormat extends RootJsonFormat[SessionId] {
    override def write(obj: SessionId): JsValue = JsString(obj.toString)
    override def read(json: JsValue): SessionId = (json: @unchecked) match {
      case JsString(x) ⇒ SessionId(x)
    }
  }
  implicit val exerciseModelFormat = jsonFormat3(ExerciseModel)
  implicit val resistanceExerciseSessionFormat = jsonFormat4(ResistanceExerciseSession)
  implicit val resistanceExerciseFormat = jsonFormat1(ResistanceExercise)
  implicit val classifiedResistanceExerciseFormat = jsonFormat6(ClassifiedResistanceExercise)
  implicit object SensorDataFormat extends JsonFormat[SensorData] {
    private val threedFormat = jsonFormat3(Threed)

    override def write(obj: SensorData): JsValue = obj match {
      case Oned(value) ⇒ JsNumber(value)
      case x: Threed ⇒ threedFormat.write(x)
    }

    override def read(json: JsValue): SensorData = json match {
      case JsNumber(value) ⇒ Oned(value.intValue())
      case _ ⇒ threedFormat.read(json)
    }
  }
  implicit val fusedSensorDataFormat = jsonFormat5(FusedSensorData)
  implicit val resistanceExerciseSetExampleFormat = jsonFormat3(ResistanceExerciseExample)
  implicit val entireResistanceExerciseSessionFormat = jsonFormat3(EntireResistanceExerciseSession)

  /**
   * Marshalling of Spark suggestions
   */
  implicit object SuggestionsToResponseMarshaller extends RootJsonFormat[Suggestions] {
    private val session = JsString("session")
    private val intensity = JsString("intensity")
    import SuggestionSource._

    implicit object SuggestionSourceFormat extends RootJsonFormat[SuggestionSource] {
      private val trainer = JsString("trainer")
      private val programme = JsString("programme")
      private val history = JsString("history")

      def suggestionSource(s: SuggestionSource): JsValue = s match {
        case Trainer(n) => JsObject("notes" → JsString(n))
        case Programme => JsString(Programme.toString.toLowerCase)
        case History => JsString(History.toString.toLowerCase)
      }

      override def write(obj: SuggestionSource): JsValue = obj match {
        case Trainer(notes) ⇒ JsObject("kind" → trainer, "notes" → JsString(notes))
        case Programme ⇒ JsObject("kind" → programme)
        case History ⇒ JsObject("kind" → history)
      }

      override def read(json: JsValue): SuggestionSource = {
        val obj = json.asJsObject
        (obj.fields("kind"): @unchecked) match {
          case `trainer` ⇒ Trainer(obj.fields("notes").toString())
          case `programme` ⇒ Programme
          case `history` ⇒ History
        }
      }
    }

    private val sessionFormat = jsonFormat4(Suggestion.Session)
    private val intensityFormat = jsonFormat4(Suggestion.Intensity)

    override def write(obj: Suggestions): JsValue = JsArray(obj.suggestions.map {
      case s: Suggestion.Session ⇒
        JsObject("kind" → session, "value" → sessionFormat.write(s))
      case i: Suggestion.Intensity =>
        JsObject("kind" → intensity, "value" → intensityFormat.write(i))
    }: _*)

    override def read(json: JsValue): Suggestions = (json: @unchecked) match {
      case JsArray(elements) ⇒ Suggestions(elements.map { element ⇒
        element.asJsObject.getFields("kind", "value") match {
          case Seq(`session`, s)   ⇒ sessionFormat.read(s)
          case Seq(`intensity`, i) ⇒ intensityFormat.read(i)
        }
      }.toList)
    }
  }

}
