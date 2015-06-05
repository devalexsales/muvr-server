package io.muvr

import spray.routing.PathMatcher1
import spray.routing.directives.PathDirectives

trait CommonPathDirectives extends PathDirectives {
  val UserIdValue: PathMatcher1[UserId] = JavaUUID.map(UserId.apply)
}
