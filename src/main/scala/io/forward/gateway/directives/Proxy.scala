package io.forward.gateway.directives

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.forward.gateway.core.backend.Backend

import scala.concurrent.ExecutionContext

object Proxy {
  def proxy(backend: Backend)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) = Route { context =>
    ???
  }
}
