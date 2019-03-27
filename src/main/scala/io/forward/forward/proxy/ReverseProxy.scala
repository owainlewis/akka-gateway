package io.forward.forward.proxy

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}

final class ReverseProxy {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val http: HttpExt = Http(system)

  private val TimeoutAccessHeader = "Timeout-Access"

  private def handler(request: HttpRequest): Future[HttpResponse] = {
    val proxyRequest = request.copy(uri="http://owainlewis.com").removeHeader(TimeoutAccessHeader)
    http.singleRequest(proxyRequest)
  }

  def start(host: String, port: Int) {
    http.bindAndHandleAsync(handler, host, port)
  }
}

