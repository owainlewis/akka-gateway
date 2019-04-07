package io.forward.switch.core.backend
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

final class WebSocketBackend extends Backend {

  private val WebSocketHeaders = Set(
    "Sec-WebSocket-Accept",
    "Sec-WebSocket-Version",
    "Sec-WebSocket-Key",
    "Sec-WebSocket-Extensions",
    "UpgradeToWebSocket",
    "Upgrade",
    "Connection"
  ).map(_.toLowerCase)

  override def apply(request: HttpRequest): Future[HttpResponse] = ???
}