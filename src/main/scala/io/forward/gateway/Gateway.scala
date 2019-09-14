package io.forward.gateway

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

final class Gateway(routes: Route)(implicit ex: ExecutionContext, system: ActorSystem, mat: ActorMaterializer) {
  // SSL enabled requires server.p12
  val ctx = new GatewayTLSContext("password")

  def start(host: String, port: Int, tls: Boolean = false): Unit = {
    val bindingFuture = if (tls) { Http().bindAndHandle(routes, host, port, connectionContext = ctx.https) } else { Http().bindAndHandle(routes, host, port) }
    bindingFuture.onComplete {
      case Success(serverBinding) => println(s"listening to ${serverBinding.localAddress}")
      case Failure(error) => println(s"error: ${error.getMessage}")
    }
  }
}

object Gateway {
  def apply(routes: Route)
  (implicit ex: ExecutionContext, system: ActorSystem, mat: ActorMaterializer): Gateway = 
    new Gateway(routes)
}
