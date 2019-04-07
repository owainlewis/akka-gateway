package io.forward.switch.filters.pre.auth

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.directives.Credentials
import io.forward.switch.filters.ComposablePreFilter
import akka.http.scaladsl.model.headers.Authorization

import scala.compat.java8.OptionConverters._
import scala.concurrent.Future

final class BasicAuthPreFilter(realm: String, password: String) extends ComposablePreFilter {

  override def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    request.getHeader(Authorization.lowercaseName).asScala match {
      case Some(_) => Future.successful(Right(request))
      case None =>  unauthorized
    }

  private def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id) if p.verify(password) => Some(id)
      case _ => None
    }

  private val unauthorized = Future.successful(Left(HttpResponse(status = StatusCodes.Unauthorized)))
}

object BasicAuthPreFilter {
  def apply(password: String) =
    new BasicAuthPreFilter("Switch", password)
}