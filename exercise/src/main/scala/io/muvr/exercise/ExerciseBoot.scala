package io.muvr.exercise

import akka.actor.ActorRef
import spray.routing.Route

import scala.concurrent.ExecutionContext

case class ExerciseBoot() {

  def route(scaffolding: ActorRef)(implicit ec: ExecutionContext): Route = ExerciseService.route(scaffolding)

}

object ExerciseBoot {
  def boot(notification: ActorRef, profile: ActorRef): ExerciseBoot = {
    ExerciseBoot()
  }
}
