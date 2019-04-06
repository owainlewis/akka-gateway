package io.forward.switch.filters

import akka.http.scaladsl.model.HttpRequest

import scala.concurrent.ExecutionContext

trait ComposablePreFilter extends PreFilter { base =>
  def ~>(filter: PreFilter)(implicit ec: ExecutionContext): ComposablePreFilter = { req: HttpRequest => {
    base.apply(req) flatMap {
      case Right(request) => filter.apply(request)
      case Left(response) => abort(response)
    }}
  }
}