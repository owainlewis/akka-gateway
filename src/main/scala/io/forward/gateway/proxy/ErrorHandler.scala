package io.forward.gateway.proxy

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.StreamTcpException

sealed trait ErrorHandler {
  val proxyExceptionHandler = ExceptionHandler {
    case e: StreamTcpException if e.getMessage.contains("Connection refused") =>
      handleProxyError(e)
  }

  private def handleProxyError(t: Throwable): Route =
    complete(HttpResponse(status = StatusCodes.BadGateway))
}
