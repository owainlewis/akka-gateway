package io.forward.switch.filters

import akka.http.scaladsl.model.HttpResponse

import scala.concurrent.Future

trait PostFilter {
  def apply(response: HttpResponse, body: String): Future[HttpResponse]
}

object NoOpPostFilter extends PostFilter {
  override def apply(response: HttpResponse, body: String): Future[HttpResponse] =
    Future.successful(response)
}
