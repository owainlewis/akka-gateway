package io.forward.switch.auth

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import io.forward.switch.filters.ComposablePreFilter

import scala.concurrent.Future

trait Auth0 {

}

object JwtAuthPreFilter extends ComposablePreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    request.getHead
    Future.successful(Right(request))
}
