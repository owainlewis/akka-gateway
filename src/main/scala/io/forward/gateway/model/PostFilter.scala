package io.forward.gateway.model

import akka.http.scaladsl.model.HttpResponse

import scala.concurrent.Future

trait PostFilter {
  /**
    * Applies a filter to a [[HttpResponse]]
    *
    * @param response The [[HttpResponse]] to modify
    * @param body An entity unmarshalled body
    */
  def onResponse(response: HttpResponse, body: String): Future[HttpResponse]
}

object NoOpPostFilter extends PostFilter {
  override def onResponse(response: HttpResponse, body: String): Future[HttpResponse] =
    Future.successful(response)
}
