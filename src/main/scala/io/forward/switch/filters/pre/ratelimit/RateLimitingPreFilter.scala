package io.forward.switch.filters.pre.ratelimit

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import io.forward.switch.filters.ComposablePreFilter

import scala.concurrent.Future

class RateLimitingPreFilter(per: Long) extends ComposablePreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    abort(HttpResponse(status = StatusCodes.TooManyRequests))
  }
}

object RateLimitingPreFilter {
  def apply(per: Long) =
    new RateLimitingPreFilter(per)
}

