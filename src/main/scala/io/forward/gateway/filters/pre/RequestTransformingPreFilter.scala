package io.forward.gateway.filters.pre

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import io.forward.gateway.model.PreFilter

import scala.concurrent.Future

/**
  * A Request transforming pre-filter is used to modify a request in some way.
  *
  * For example it might add HTTP headers or modify the request entity itself
  *
  * @param transformer A [[RequestTransformer]]
  */
class RequestTransformingPreFilter(transformer: RequestTransformer) extends PreFilter {
  def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    continue(transformer.transform(request))
}

object RequestTransformingPreFilter {
  def apply(transformer: RequestTransformer) =
    new RequestTransformingPreFilter(transformer)
}