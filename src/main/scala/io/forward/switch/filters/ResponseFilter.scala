package io.forward.switch.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, ResponseEntity}

import scala.concurrent.Future

trait ResponseFilter[-RequestData] {
  def apply(request: HttpRequest, response: HttpResponse, data: RequestData): Future[HttpResponse]
}

object NoOpResponseFilter extends ResponseFilter[ResponseEntity] {
  def apply(request: HttpRequest, response: HttpResponse, data: ResponseEntity): Future[HttpResponse] =
    Future.successful(response)
}
