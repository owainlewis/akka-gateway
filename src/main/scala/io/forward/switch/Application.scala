package io.forward.switch

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.forward.switch.core.HttpUpstream
import io.forward.switch.filters.{HeaderModifyingRequestFilter, NoOpRequestFilter}

import scala.concurrent.ExecutionContext

object Application extends App {
  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val http = Http(system)

  object Upstreams {
    val fooUpstream = new HttpUpstream("https://hookb.in/XkLMbV8x0YubobmZEm1m", Some(HeaderModifyingRequestFilter))
    val barUpstream = new HttpUpstream("https://jsonplaceholder.typicode.com/todos/1")
  }

  val routes: Route =
    path("foo") {
      get {
          val filterChain = new FilterChain(NoOpPreFilter, NoOpPostFilter)
          filterChain.apply(Upstreams.fooUpstream)
        }
    }

  /** Run the server **/
  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}
