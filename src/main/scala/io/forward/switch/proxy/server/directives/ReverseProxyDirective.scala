package io.forward.switch.proxy.server.directives

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

trait ReverseProxyDirective {
  type RequestExecutor = ((HttpRequest) ⇒ Future[HttpResponse])

}  