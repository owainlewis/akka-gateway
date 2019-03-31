package io.forward.switch

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import io.forward.switch.core.Upstream

import scala.concurrent.ExecutionContext
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

final class HttpProxy()(implicit e: ExecutionContext, m: Materializer) {
}