package io.forward.switch.filters

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

trait RequestFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]]
}

object NoOpRequestFilter extends RequestFilter {
  override def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    Future.successful(Left(HttpResponse()))
}

object HeaderModifyingRequestFilter extends RequestFilter {
  override def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    Future.successful(Right(request.addHeader(RawHeader("Foo", "Bar"))))
}