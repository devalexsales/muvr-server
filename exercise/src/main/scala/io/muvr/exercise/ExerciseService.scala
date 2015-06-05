package io.muvr.exercise

import akka.actor.ActorRef
import io.muvr.exercise.UserExerciseProcessor.{ExerciseSubmitEntireResistanceExerciseSession, Foo}
import io.muvr.{CommonMarshallers, CommonPathDirectives}
import spray.routing._

import scala.concurrent.ExecutionContext

/**
 * REST API for the exercise service. Exposes endpoints that allow the clients to submit entire exercise sessions, to
 * submit exercise examples
 */
private[exercise] object ExerciseService extends Directives with ExerciseProtocolMarshallers with CommonMarshallers with CommonPathDirectives {
  import akka.pattern.ask
  import io.muvr.CommonMarshallers._
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
    } ~
    path("exercise" / UserIdValue / "foo") { userId ⇒
      get {
        complete {
          exercise ! Foo(userId)
        }
      }
    }

}
