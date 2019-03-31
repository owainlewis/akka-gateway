package io.forward.switch.core

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Host, `Timeout-Access`}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.Materializer
import io.forward.switch.filters.RequestFilter

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait Upstream {
  def apply(request: HttpRequest): Future[HttpResponse]
}

class HttpUpstream[T](target: Uri, requestFilter: Option[RequestFilter] = None)(implicit system: ActorSystem, ex: ExecutionContext, materializer: Materializer) extends Upstream {
  private val defaultTimeout = 20.seconds

  def address(request: HttpRequest): HttpRequest = {
    val initialRequest = request.copy().removeHeader(`Timeout-Access`.name)
    val headers = initialRequest.headers.filterNot(t => t.name() == "Host") :+ Host(target.authority.host)
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

    requestFilter match {
      case Some(filter) => filter.apply(proxyRequest) flatMap {
        case Left(resp) => Future.successful(resp)
        case Right(req) => proxyHttpRequest(req)
      }
      case None => proxyHttpRequest(proxyRequest)
    }
  }

  private def proxyHttpRequest(request: HttpRequest): Future[HttpResponse] =
    Http(system).singleRequest(request) flatMap { response =>
      response.entity.withoutSizeLimit().toStrict(defaultTimeout).flatMap { _ =>
        Future.successful(response)
      }
    }
}
