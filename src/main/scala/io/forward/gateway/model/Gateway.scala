package io.forward.gateway.model

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._
import io.forward.gateway.core.backend.Backend
import io.forward.gateway.filters.{NoOpPostFilter, NoOpPreFilter, PostFilter, PreFilter}
import io.circe._
import io.circe.generic.semiauto._

/**
  * An API Gateway is a type that contains routes mapping to various backends.
  *
  * @param routes A sequence of routes
  */
case class Gateway(routes: Seq[Route])

/**
  * A route defines the logic binding a HTTP method and path to a backend.
  *
  * An optional pre and post filter can be applied to the route to modify the request
  * or response
  *
  * @param method The HTTP method to match
  * @param path A path
  * @param backend A [[Backend]]
  * @param preFilter A [[PreFilter]] to apply to the request
  * @param postFilter A [[PostFilter]] to apply to the response
  */
case class Route(method: HttpRequestMethod,
                 path: String,
                 backend: Backend,
                 preFilter: PreFilter = NoOpPreFilter,
                 postFilter: PostFilter = NoOpPostFilter)

sealed trait HttpRequestMethod {
  def asMethodDirective: Directive0 = this match {
    case HttpRequestMethod.Get => get
    case HttpRequestMethod.Put => put
    case HttpRequestMethod.Post => post
    case HttpRequestMethod.Delete => delete
    case HttpRequestMethod.Options => options
  }
}

object HttpRequestMethod {
  case object Get extends HttpRequestMethod
  case object Put extends HttpRequestMethod
  case object Post extends HttpRequestMethod
  case object Delete extends HttpRequestMethod
  case object Options extends HttpRequestMethod

  implicit val httpRequestMethodDecoder: Decoder[HttpRequestMethod] = deriveDecoder[HttpRequestMethod]
}




