package io.forward.gateway.auth

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import io.forward.gateway.filters.ComposablePreFilter

import scala.concurrent.Future

trait Auth0 {

}

object JwtAuthPreFilter extends ComposablePreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    Future.successful(Right(request))
}
