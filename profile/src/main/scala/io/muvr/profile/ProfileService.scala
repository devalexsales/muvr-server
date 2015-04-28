package io.muvr.profile

import java.util.UUID

import akka.actor.ActorRef
import io.muvr.{UserId, CommonPathDirectives, CommonMarshallers}
import io.muvr.notification.NotificationProtocol.{AndroidDevice, IOSDevice}
import io.muvr.profile.UserProfileProcessor._
import io.muvr.profile.UserProfileProtocol._
import spray.http._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

private[profile] object ProfileService extends Directives with SprayJsonSupport with CommonMarshallers with CommonPathDirectives {
  import akka.pattern.ask
  import io.muvr.Timeouts.defaults._
  import DefaultJsonProtocol._

  private implicit val userRegisterFormat = jsonFormat2(UserRegister)
  private implicit val userLoginFormat = jsonFormat2(UserLogin)
  private implicit val publicProfileFormat = jsonFormat4(PublicProfile)
  private implicit val iosDeviceFormat = jsonFormat(IOSDevice, "deviceToken")
  private implicit val androidDeviceFormat = jsonFormat0(AndroidDevice)

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
          (userProfileProcessor ? UserSetPublicProfile(userId, publicProfile)).mapRight[Unit]
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
