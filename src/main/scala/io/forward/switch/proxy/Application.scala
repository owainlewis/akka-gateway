package io.forward.switch.proxy

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.model.headers.`Timeout-Access`

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Route}
import io.forward.forward.proxy.filters.NoOpResponseFilter

import scala.util.{Failure, Success}
import scala.concurrent.duration._

trait Upstream {
  def target: Uri
  def transformRequest(request: HttpRequest): HttpRequest = request
  def responseTransformer(response: HttpResponse): HttpResponse = response
}

object SimpleUpstream extends Upstream {
  def target = "http://requestbin.fullcontact.com/1frlbkp1"

  override def responseTransformer(response: HttpResponse): HttpResponse = response.withEntity("Modified Response")
}

object Application extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config = ConfigFactory.load()

  val route: Route = {
    get {
      proxyTo(SimpleUpstream)
    }
  }

  private def proxyTo(upstream: Upstream) =
    extractRequest { request =>
      val responseFuture = proxyHandler(request, upstream)
      onComplete(responseFuture) {
        case Success(response) => complete(response)
        case Failure(_) => complete("Internal server error")
      }
    }

  val entityConsumptionTimeout = 20.seconds

  private def proxyHandler(request: HttpRequest, upstream: Upstream): Future[HttpResponse] = {
    val proxyRequest =
      request.withUri(upstream.target)
      .removeHeader(`Timeout-Access`.name)
    val upstreamResponse = Http().singleRequest(proxyRequest)

    upstreamResponse flatMap { response =>
      response.entity.withoutSizeLimit().toStrict(entityConsumptionTimeout).flatMap { entity =>
        NoOpResponseFilter(request, response, entity)
      }
    }
  }

  Http().bindAndHandle(route, "localhost", 8080)
}
