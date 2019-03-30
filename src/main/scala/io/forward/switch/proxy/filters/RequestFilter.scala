package io.forward.forward.proxy.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

trait ResponseFilter[-RequestData] {
  def apply(request: HttpRequest, response: HttpResponse, data: RequestData): Future[HttpResponse]
}

object NoOpResponseFilter extends ResponseFilter[Any] {
  def apply(request: HttpRequest, response: HttpResponse, data: Any): Future[HttpResponse] =
    Future.successful(response)
}
