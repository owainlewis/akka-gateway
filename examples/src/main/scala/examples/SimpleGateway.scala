package examples

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import io.forward.switch.core.backend.HttpBackend
import io.forward.switch.filters._
import io.forward.switch.filters.pre.auth.BasicAuthPreFilter

import scala.concurrent.ExecutionContext

object SimpleGateway extends App with DefaultImplicits {

  val routes: Route =
    path("foo") {
      get {
        FilterChain(BasicAuthPreFilter("password"), HttpBackend("https://postman-echo.com/get"), NoOpPostFilter)
      }
    }

  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}

sealed trait DefaultImplicits {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val http: HttpExt = Http(system)
}