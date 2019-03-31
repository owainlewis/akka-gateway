package io.forward.switch

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.forward.switch.core.Upstream

import scala.util.{Failure, Success}

trait ReverseProxy {

  def proxyTo(upstream: Upstream): Route =
    extractRequest { request =>
      onComplete(upstream.apply(request)) {
        case Success(response) => complete(response)
        case Failure(_) => complete("Internal server error")
      }
    }
}