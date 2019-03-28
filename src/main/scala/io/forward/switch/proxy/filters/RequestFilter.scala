package io.forward.forward.proxy.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import scala.concurrent.Future

trait RequestFilter {
  def filter(request: HttpRequest): Future[HttpResponse]
}
