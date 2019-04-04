package io.forward.switch

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.forward.switch.core.Upstream
import io.forward.switch.filters.{RequestFilter, ResponseFilter}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

sealed trait ReverseProxy {
  def proxyTo(upstream: Upstream, requestFilter: Option[RequestFilter] = None)(implicit ec: ExecutionContext): Route =
    extractRequest { request =>
      val x = requestFilter.map(_.apply(request))
      dispatch(upstream, request)
    }

  private def dispatch(upstream: Upstream, request: HttpRequest) =
    onComplete(upstream.apply(request)) {
      case Success(response) => complete(response)
      case Failure(_) => complete("Internal server error")
    }
}