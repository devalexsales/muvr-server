package io.muvr

import spray.http._
import spray.httpx.marshalling.{ToResponseMarshallingContext, ToResponseMarshaller}
import spray.httpx.unmarshalling._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{\/, \/-, -\/}

/**
 * Common marshallers companion
 */
object CommonMarshallers {

  /**
   * Response option follows the standard ``Option[A]`` style
   */
  private object ResponseOption {
    def apply[A](o: Option[A]): ResponseOption[A] = o match {
      case Some(v) ⇒ ResponseSome(v)
      case None    ⇒ ResponseNone
    }
  }

  /**
   * The option structure
   * @tparam A the A
   */
  sealed trait ResponseOption[+A]
  /** None */
  case object ResponseNone extends ResponseOption[Nothing]
  /** Some */
  case class ResponseSome[A](value: A) extends ResponseOption[A]

  /**
   * Pairs together the unmarshalled value and the entity that produced the ``unmarshalled``
   *
   * @param unmarshalled the unmarshalled value
   * @param entity the entity that produced ``unmarshalled``
   * @tparam A the type of A
   */
  case class UnmarshalledAndEntity[A](unmarshalled: A, entity: HttpEntity)

}

trait CommonMarshallers {
  import CommonMarshallers._

  implicit class DisjunctUnionFuture(f: Future[_]) {

    def mapRight[B]: Future[\/[String, B]] = f.mapTo[\/[String, B]]

    def mapNoneToEmpty[B](implicit ec: ExecutionContext): Future[ResponseOption[B]] = f.mapTo[Option[B]].map(ResponseOption.apply)

  }

  implicit def duToResponseMarshaller[A : ToResponseMarshaller, B : ToResponseMarshaller]: ToResponseMarshaller[\/[A, B]] = {
    val lm = implicitly[ToResponseMarshaller[A]]
    val rm = implicitly[ToResponseMarshaller[B]]

    new ToResponseMarshaller[\/[A, B]] {
      override def apply(value: \/[A, B], ctx: ToResponseMarshallingContext): Unit = value match {
        case \/-(right) ⇒
          rm.apply(right, ctx)
        case -\/(left)  ⇒
          // TODO: BadRequest?!
          lm.apply(left, ctx.withResponseMapped(_.copy(status = StatusCodes.BadRequest)))
      }
    }
  }

  implicit def responseOptionMarshaller[A : ToResponseMarshaller]: ToResponseMarshaller[ResponseOption[A]] = {
    val m = implicitly[ToResponseMarshaller[A]]

    new ToResponseMarshaller[ResponseOption[A]] {
      override def apply(value: ResponseOption[A], ctx: ToResponseMarshallingContext): Unit = value match {
        case ResponseNone    ⇒ ctx.marshalTo(HttpResponse(StatusCodes.OK, entity = HttpEntity(contentType = ContentTypes.`application/json`, string = "{}")))
        case ResponseSome(a) ⇒ m.apply(a, ctx)
      }
    }
  }

  implicit def unmarshalledAndEntityFRU[A : FromRequestUnmarshaller]: FromRequestUnmarshaller[UnmarshalledAndEntity[A]] = {
    val um = implicitly[FromRequestUnmarshaller[A]]

    new FromRequestUnmarshaller[UnmarshalledAndEntity[A]] {
      override def apply(request: HttpRequest): Deserialized[UnmarshalledAndEntity[A]] = um.apply(request) match {
        case Right(a) ⇒ Right(UnmarshalledAndEntity(a, request.entity))
        case Left(l) ⇒ Left(l)
      }
    }
  }

}
