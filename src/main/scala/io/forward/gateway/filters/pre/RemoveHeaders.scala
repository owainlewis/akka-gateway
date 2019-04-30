package io.forward.gateway.filters.pre

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import io.forward.gateway.model.PreFilter

import scala.concurrent.Future

final class RemoveHeaders(headers: Seq[String]) extends PreFilter {
  def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    val requestHeaders = request.headers
    continue(request.withHeaders(requestHeaders.filterNot(p => headers.map(_.toLowerCase()).contains(p.lowercaseName()))))
  }
}

object RemoveHeaders {
  def apply(headers: Seq[String]): PreFilter = {
    new RemoveHeaders(headers)
  }
}
