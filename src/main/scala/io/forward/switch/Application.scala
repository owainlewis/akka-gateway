package io.forward.switch

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.forward.switch.core.HttpUpstream

import scala.concurrent.ExecutionContext

object Application extends App with ReverseProxy {
  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val http = Http(system)

  object Upstreams {
    val fooUpstream = new HttpUpstream("https://jsonplaceholder.typicode.com/todos")
    val barUpstream = new HttpUpstream("https://jsonplaceholder.typicode.com/todos/1")
  }

  val routes: Route =
    path("foo") {
      get {
        extractRequestContext { ctx =>
          ctx.log.info("Proxying to Foo upstream")
          proxyTo(Upstreams.fooUpstream)
        }
      }
    } ~ path("bar") {
      get {
        proxyTo(Upstreams.barUpstream)
      }
    }

  /** Run the server **/
  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}
