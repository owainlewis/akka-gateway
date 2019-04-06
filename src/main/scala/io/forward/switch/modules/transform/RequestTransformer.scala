package io.forward.switch.modules.transform

import akka.http.scaladsl.model.HttpRequest

trait RequestTransformer {
  def transform(httpRequest: HttpRequest): HttpRequest
}
