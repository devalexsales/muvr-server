package io.muvr.profile

import akka.actor.ActorRef
import io.muvr.notification.NotificationProtocol.{AndroidDevice, IOSDevice}
import io.muvr.profile.UserProfileProcessor._
import io.muvr.profile.UserProfileProtocol._
import io.muvr.{CommonMarshallers, CommonPathDirectives, UserId}
import spray.http._
import spray.routing._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

private[profile] object ProfileService extends Directives with ProfileMarshallers with CommonPathDirectives with CommonMarshallers {
  import akka.pattern.ask
  import io.muvr.Timeouts.defaults._

  def userProfileRoute(userProfile: ActorRef, userProfileProcessor: ActorRef)(implicit ec: ExecutionContext) =
    path("user") {
      post {
        handleWith { register: UserRegister ⇒
          (userProfileProcessor ? register).mapRight[UserId]
        }
      } ~
      put {
        handleWith { login: UserLogin ⇒
          (userProfileProcessor ? login).mapRight[UserId]
        }
      }
    } ~
    path("user" / UserIdValue) { userId ⇒
      get {
        complete {
          (userProfile ? UserGetPublicProfile(userId)).mapNoneToEmpty[PublicProfile]
        }
      } ~
      post {
        handleWith { publicProfile: PublicProfile ⇒
          userProfileProcessor ! UserSetPublicProfile(userId, publicProfile)
        }
      }
    } ~
    path("user" / UserIdValue / "check") { userId ⇒
      get {
        complete {
          (userProfileProcessor ? UserCheckAccount(userId)).mapTo[Boolean].map { x ⇒
            if (x) HttpResponse(StatusCodes.OK) else HttpResponse(StatusCodes.NotFound)
          }
        }
      }
    } ~
    path("user" / UserIdValue / "image") { userId ⇒
      get {
        complete {
          (userProfile ? UserGetProfileImage(userId)).mapTo[Option[Array[Byte]]].map { x ⇒
            HttpResponse(entity = HttpEntity(contentType = ContentType(MediaTypes.`image/png`), bytes = x.getOrElse(Array.empty)))
          }
        }
      } ~
      post {
        ctx ⇒
          val image = ctx.request.entity.data.toByteArray
          (userProfileProcessor ? UserSetProfileImage(userId, image)).onComplete {
            case Success(_) ⇒ ctx.complete(HttpResponse(StatusCodes.OK))
            case Failure(_) ⇒ ctx.complete(HttpResponse(StatusCodes.InternalServerError))
          }
      }
    } ~
    path("user" / UserIdValue / "device" / "ios") { userId ⇒
      post {
        handleWith { device: IOSDevice ⇒
          (userProfileProcessor ? UserSetDevice(userId, device)).mapRight[Unit]
        }
      }
    } ~
    path("user" / UserIdValue / "device" / "android") { userId ⇒
      post {
        handleWith { device: AndroidDevice ⇒
          (userProfileProcessor ? UserSetDevice(userId, device)).mapRight[Unit]
        }
      }
    }

}
