package io.forward.gateway.directives

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.Materializer
import io.forward.gateway.model.Backend

import scala.concurrent.ExecutionContext

object Proxy {
  def proxy(backend: Backend)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer): Route =
    runBackendRequest(backend) { response =>
      complete(response)
    }

  private def runBackendRequest(backend: Backend): Directive1[HttpResponse] = {
    Directive { inner => ctx =>
      implicit val ex: ExecutionContext = ctx.executionContext
      backend.apply(ctx.request).flatMap { result =>
        inner(Tuple1(result))(ctx)
      }
    }
  }
}
