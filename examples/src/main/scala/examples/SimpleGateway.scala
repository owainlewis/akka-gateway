package examples

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import io.forward.gateway.Gateway
import io.forward.gateway.core.backend.HttpBackend
import io.forward.gateway.filters.request.RemoveHeaders

import scala.concurrent.ExecutionContext

object SimpleGateway extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  import io.forward.gateway.directives.Filter._
  import io.forward.gateway.directives.Proxy._

  val backend = new HttpBackend("https://postman-echo.com/get")

  val headerFilter = RemoveHeaders(Seq("Authorization"))

  val route = pathSingleSlash {
    get {
      withRequestFilters(headerFilter) {
        proxy(backend)
      }
    }
  }

  val service = Gateway(route)

  service.start("localhost", 8080)
}
