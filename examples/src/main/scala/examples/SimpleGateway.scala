package examples

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import io.forward.gateway.core.backend._
import io.forward.gateway.filters._
import io.forward.gateway.filters.pre.auth.BasicAuthPreFilter
import io.forward.gateway.model.{Get, HttpRequestMethod}
import io.forward.gateway.modules.loadbalance.RoundRobin

import scala.concurrent.ExecutionContext

object SimpleGateway extends App with DefaultImplicits {

  val balancer = new RoundRobin(Seq("https://google.com", "https://owainlewis.com", "https://postman-echo.com/get"))

  // Random ideas

  def route(pathMatch: String, method: HttpRequestMethod): Route =
    path(pathMatch) {
      method.asMethodDirective {
        FilterChain(BasicAuthPreFilter("password"), LoadBalancedHttpBackend(balancer), NoOpPostFilter)
      }
    }

  val routes: Route = route("foo", Get)

  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}

sealed trait DefaultImplicits {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val http: HttpExt = Http(system)
}