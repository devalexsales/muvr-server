package io.muvr.profile

import io.muvr.{CommonPathDirectives, CommonMarshallers, UserId}
import io.muvr.notification.NotificationProtocol.{AndroidDevice, IOSDevice}
import io.muvr.profile.UserProfileProcessor.{UserLogin, UserRegister}
import io.muvr.profile.UserProfileProtocol.PublicProfile
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, RootJsonFormat}

trait ProfileMarshallers extends SprayJsonSupport with CommonMarshallers {
  implicit val userRegisterFormat = jsonFormat2(UserRegister)
  implicit val userLoginFormat = jsonFormat2(UserLogin)
  implicit val publicProfileFormat = jsonFormat4(PublicProfile)
  implicit val iosDeviceFormat = jsonFormat(IOSDevice, "deviceToken")
  implicit val androidDeviceFormat = jsonFormat0(AndroidDevice)

  implicit object UserIdIdFormat extends RootJsonFormat[UserId] {
    override def write(obj: UserId): JsValue = JsString(obj.toString)
    override def read(json: JsValue): UserId = (json: @unchecked) match {
      case JsString(x) ⇒ UserId(x)
    }
  }

}
