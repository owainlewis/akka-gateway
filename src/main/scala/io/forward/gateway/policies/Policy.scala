package io.forward.gateway.policies

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, OptHttpRequest}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives._

trait Policy

trait RequestPolicy extends Policy {
  def onRequest: Directive1[String] =
    Directive { inner => ctx =>
      inner(Tuple1("FOO"))(ctx)
    }
}

trait ResponsePolicy extends Policy {
  def onResponse(response: HttpResponse)
}
