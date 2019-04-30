package io.forward.gateway.filters.pre

import akka.http.scaladsl.model.HttpRequest

trait RequestTransformer {
  def transform(httpRequest: HttpRequest): HttpRequest
}
