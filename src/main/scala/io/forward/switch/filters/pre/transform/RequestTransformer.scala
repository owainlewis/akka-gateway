package io.forward.switch.filters.pre.transform

import akka.http.scaladsl.model.HttpRequest

trait RequestTransformer {
  def transform(httpRequest: HttpRequest): HttpRequest
}
