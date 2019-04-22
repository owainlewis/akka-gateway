package io.forward.gateway.filters.pre

import akka.http.scaladsl.model.RemoteAddress.IP
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import io.forward.gateway.filters.ComposablePreFilter

import scala.concurrent.Future

class IPWhiteListPreFilter(ips: Seq[IP]) extends ComposablePreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    continue(request)
}

object RequestTransformingPreFilter {
  def apply(ips: Seq[IP]) =
    new IPWhiteListPreFilter(ips)
}
