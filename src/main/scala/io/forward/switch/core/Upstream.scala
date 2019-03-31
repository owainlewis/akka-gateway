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

class HttpUpstream(target: Uri)(implicit system: ActorSystem,
                                ex: ExecutionContext,
                                materializer: Materializer) extends Upstream {
  private val defaultTimeout = 20.seconds

  def address(request: HttpRequest): HttpRequest = {
    val initialRequest = request.copy().removeHeader(`Timeout-Access`.name)
    val headers = initialRequest.headers.filterNot(t => t.name() == "Host") :+ Host(target.authority.host)
    request.copy().withHeaders(headers).withUri(target)
  }

  /**
    * TODO investigate connection reset error here with entity
    *
    * @param request
    * @param system
    * @param ex
    * @param materializer
    * @return
    */
  def apply(request: HttpRequest): Future[HttpResponse] = {
    val outboundRequest = address(request)
    val response = Http().singleRequest(outboundRequest)
    response.flatMap { r =>
      r.entity.withoutSizeLimit().toStrict(defaultTimeout).flatMap { e =>
        response
      }
    }
  }
}