package io.forward.switch.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import scala.concurrent.Future

trait PostFilter[-RequestData] {
  def apply(request: HttpRequest, response: HttpResponse, data: RequestData): Future[HttpResponse]
}

object NoOpPostFilter extends PostFilter[Unit] {
  override def apply(request: HttpRequest, response: HttpResponse, data: Unit): Future[HttpResponse] =
    Future.successful(HttpResponse(status = StatusCodes.BadGateway))
}