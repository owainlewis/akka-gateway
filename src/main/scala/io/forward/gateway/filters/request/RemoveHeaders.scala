package io.forward.gateway.filters.request

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import io.forward.gateway.model.RequestFilter

import scala.concurrent.Future

/**
  * A filter that will remove HTTP headers from an incoming request
  *
  * @param headers A sequence of headers to strip out
  */
final class RemoveHeaders(headers: Seq[String]) extends RequestFilter {
  def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    val desiredHeaders = request.headers.filterNot { p =>
      headers.map(_.toLowerCase()).contains(p.lowercaseName())
    }
    continue(request.withHeaders(desiredHeaders))
  }
}

object RemoveHeaders {
  def apply(headers: Seq[String]): RequestFilter = {
    new RemoveHeaders(headers)
  }
}
