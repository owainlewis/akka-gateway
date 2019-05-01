package io.forward.gateway.model

import akka.http.scaladsl.model.HttpResponse

import scala.concurrent.Future

trait ResponseFilter {
  /**
    * Applies a filter to a [[HttpResponse]]
    *
    * @param response The [[HttpResponse]] to modify
    */
  def onResponse(response: HttpResponse): Future[HttpResponse]
}

object NoOpPostFilter extends ResponseFilter {
  override def onResponse(response: HttpResponse): Future[HttpResponse] =
    Future.successful(response)
}
