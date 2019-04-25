package io.forward.gateway.proxy

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Host, `Timeout-Access`}
import akka.http.scaladsl.model.{HttpResponse, Uri}
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.stream.Materializer

import scala.concurrent.{ExecutionContext, Future}

final class Client(val target: Uri)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) {

  val route = Route { context =>
    proxyRequest(context).flatMap(context.complete(_))
  }

  private def proxyRequest(context: RequestContext): Future[HttpResponse] = {
    val request = context.request.copy().removeHeader(`Timeout-Access`.name)
    val headers = request.headers.filterNot(_.name() == `Host`.name) :+ Host(target.authority.host)
    val newRequest = context.request.copy(headers = headers).withUri(target)
    println("Opening connection to " + request.uri.authority.host.address)
    Http(system).singleRequest(newRequest)
  }
}
