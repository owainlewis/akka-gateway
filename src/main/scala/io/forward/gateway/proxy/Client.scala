package io.forward.gateway.proxy

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Host
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.{ExecutionContext, Future}

final class Client(val host: String, val port: Int)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) {

  val route = Route { context =>
    proxyRequest(context)
  }

  private val connector = Http(system).outgoingConnection(host, port)

  private def proxyRequest(context: RequestContext): Future[RouteResult] = {
    val newHeaders = context.request.headers.filterNot(_.name() == `Host`.name) :+ Host(host,port)
    val newRequest = context.request.copy(headers = newHeaders)
    Source.single(newRequest)
      .via(connector).runWith(Sink.head)
      .flatMap(context.complete(_))
  }
}
