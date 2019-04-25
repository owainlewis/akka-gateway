package io.forward.gateway.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.ExecutionContext

trait ComposablePreFilter extends PreFilter { base =>
  def ~>(filter: PreFilter)(implicit ec: ExecutionContext): ComposablePreFilter = { req: HttpRequest => {
    base.onRequest(req) flatMap {
      case Right(request) => filter.onRequest(request)
      case Left(response) => abort(response)
    }}
  }
}

trait ComposablePostFilter extends PostFilter { base =>
  // TODO
}