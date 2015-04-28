package io.muvr.exercise

import akka.actor.ActorRef
import spray.http.HttpEntity
import spray.routing.Directives

private[exercise] object ExerciseService extends Directives {

  def route(scaffolding: ActorRef) =
    path("exercise" / Segment / Segment) { (x, y) ⇒
      post {
        handleWith { body: HttpEntity ⇒
          scaffolding ! body.asString
          "{}"
        }
      }
    }

}
