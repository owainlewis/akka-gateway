package io.forward.switch.core.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Host, `Timeout-Access`}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.Materializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

final class HttpBackend(target: Uri, entityTimeout: FiniteDuration = 10.seconds)
                          (implicit
                           system: ActorSystem,
                           ex: ExecutionContext,
                           materializer: Materializer) extends Backend {
  /**
    * Prior to dispatching a request, strip headers and make initial modifications
    *
    * @param request An [[HttpRequest]] to address
    * @return A [[HttpRequest]] to dispatch
    */
  def address(request: HttpRequest): HttpRequest = {
    val initialRequest = request.copy().removeHeader(`Timeout-Access`.name)
    val headers = initialRequest.headers.filterNot(_.name() == `Host`.name) :+ Host(target.authority.host)
    request.copy().withHeaders(headers).withUri(target)
  }

  /**
    * TODO investigate connection reset error here with entity
    *
    * Apply all request filters, dispatch the request and run response filters when appropriate
    *
    * @param request A HTTP request to proxy
    * @param system
    * @param ex
    * @param materializer
    * @return
    */
  def apply(request: HttpRequest): Future[HttpResponse] =
    Http(system).singleRequest(address(request))
}

object HttpBackend {
  def apply(target: Uri)
           (implicit system: ActorSystem, ex: ExecutionContext, materializer: Materializer) =
    new HttpBackend(target)
}

