package io.forward.switch.modules.auth0

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Authorization
import io.forward.switch.filters.PreFilter

import scala.compat.java8.OptionConverters._
import scala.concurrent.Future

class JWTPreFilter extends PreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    val token = request.getHeader(Authorization.lowercaseName).asScala.flatMap(_.value().split("Bearer").drop(1).headOption)

    token match {
      case Some(authToken) => Future.successful(Left(HttpResponse(entity = HttpEntity(authToken))))
      case None => Future.successful(Left(HttpResponse(status = StatusCodes.Unauthorized)))
    }
  }
}
