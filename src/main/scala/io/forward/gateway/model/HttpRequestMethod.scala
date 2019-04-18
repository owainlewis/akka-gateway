package io.forward.gateway.model

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._

sealed trait HttpRequestMethod {
  def asMethodDirective: Directive0 = this match {
    case Get => get
    case Put => put
  }
}

case object Get extends HttpRequestMethod

case object Put extends HttpRequestMethod

case object Post extends HttpRequestMethod

case object Delete extends HttpRequestMethod

case object Option extends HttpRequestMethod
