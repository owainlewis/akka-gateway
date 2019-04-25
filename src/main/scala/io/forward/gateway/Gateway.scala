package io.forward.gateway

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

final class Gateway(routes: Route)(implicit ex: ExecutionContext, system: ActorSystem, mat: ActorMaterializer) {
  def start(host: String, port: Int): Unit = {
    val bindingFuture = Http().bindAndHandle(routes, host, port)
    bindingFuture.onComplete {
      case Success(serverBinding) => println(s"listening to ${serverBinding.localAddress}")
      case Failure(error) => println(s"error: ${error.getMessage}")
    }
  }
}

object Gateway {
  def apply(routes: Route)(implicit ex: ExecutionContext, system: ActorSystem, mat: ActorMaterializer): Gateway = new Gateway(routes)
}