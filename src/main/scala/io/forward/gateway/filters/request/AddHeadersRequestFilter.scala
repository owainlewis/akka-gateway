package io.forward.gateway.filters.request

import akka.http.scaladsl.model.{HttpHeader, HttpRequest, HttpResponse}
import io.forward.gateway.model.RequestFilter

import scala.concurrent.Future

final class AddHeadersRequestFilter(headers: HttpHeader*) extends RequestFilter {
  def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    val requestWithAdditionalHeaders = headers.foldLeft(request)((r,v) => r.addHeader(v))
    continue(requestWithAdditionalHeaders)
  }
}
