package io.forward.switch.filters

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

trait RequestFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]]

  /**
    * Halt a request chain by immediately existing and returning a response
    *
    * @param response The [[akka.http.scaladsl.model.HttpResponse]] to return
    *
    * @return
    */
  def halt(response: HttpResponse): Future[Left[HttpResponse, Nothing]] = Future.successful(Left(response))
}

object NoOpRequestFilter extends RequestFilter {
  override def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    halt(HttpResponse())
}

object HeaderModifyingRequestFilter extends RequestFilter {
  override def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    Future.successful(Right(request.addHeader(RawHeader("Foo", "Bar"))))
}

final class MockResponseRequestFilter(mockResponse: HttpResponse) extends RequestFilter {
  override def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    halt(mockResponse)
}