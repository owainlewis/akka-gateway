package io.forward.switch.core

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Host, `Timeout-Access`}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.Materializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait Upstream {
  def apply(request: HttpRequest): Future[HttpResponse]
}

class HttpUpstream[T](target: Uri)(implicit system: ActorSystem, ex: ExecutionContext, materializer: Materializer) extends Upstream {
  private val defaultTimeout = 20.seconds

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
  def apply(request: HttpRequest): Future[HttpResponse] = {
    val proxyRequest = address(request)
    proxyHttpRequest(proxyRequest)
  }

  private def proxyHttpRequest(request: HttpRequest): Future[HttpResponse] =
    Http(system).singleRequest(request) flatMap { response =>
      response.entity.withoutSizeLimit().toStrict(defaultTimeout).flatMap { _ =>
        Future.successful(response)
      }
    }
}
