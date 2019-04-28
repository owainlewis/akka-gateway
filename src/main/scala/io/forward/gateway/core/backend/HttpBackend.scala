package io.forward.gateway.core.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Host, `Timeout-Access`}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.Materializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HttpBackend(target: Uri, entityTimeout: FiniteDuration = 10.seconds)
                 (implicit system: ActorSystem, ex: ExecutionContext, mat: Materializer) extends Backend {
  /**
    * Dispatch a request and return the response
    *
    * @param request A HTTP request to proxy
    */
  def apply(request: HttpRequest): Future[HttpResponse] =
    Http(system).singleRequest(address(request))

  private def address(request: HttpRequest): HttpRequest = {
    val initialRequest = request.copy().removeHeader(`Timeout-Access`.name)
    val headers = initialRequest.headers.filterNot(_.name() == `Host`.name) :+ Host(target.authority.host)
    request.copy().withHeaders(headers).withUri(target)
  }
}

object HttpBackend {
  def apply(target: Uri)
           (implicit system: ActorSystem, ex: ExecutionContext, materializer: Materializer) =
    new HttpBackend(target)
}

