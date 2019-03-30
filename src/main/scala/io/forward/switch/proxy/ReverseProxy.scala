package io.forward.switch.proxy

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContextExecutor, Future}

class ReverseProxy {

  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val http: HttpExt = Http(system)

  private val TimeoutAccessHeader = "Timeout-Access"

  val proxyRoute: Route = (get | put) {
    extractRequest { request =>
      onComplete(proxyHandler(request)) {
        case Success(response) => complete(response)
        case Failure(_) => complete("Internal server error")
      }
    }
  }

  private def proxyHandler(request: HttpRequest): Future[HttpResponse] = {
    val proxyRequest = request.copy(uri="http://owainlewis.com").removeHeader(TimeoutAccessHeader)
    http.singleRequest(proxyRequest)
  }

  /**
    * Start the proxy server on a given interface and port
    *
    * @param interface
    * @param port
    */
  def start(interface: String, port: Int) {
    Http().bindAndHandle(proxyRoute, interface, port)
  }
}

