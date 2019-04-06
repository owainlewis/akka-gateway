package io.forward.switch

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.{get, path}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.forward.switch.core.HttpUpstream
import io.forward.switch.filters.{NoOpPostFilter, RequestTransformingPreFilter}
import io.forward.switch.modules.transform.{HeaderTransformer, RequestTransformer}

import scala.concurrent.ExecutionContext

object Application extends App {
  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val http = Http(system)

  object Upstreams {
    val fooUpstream = new HttpUpstream("https://postman-echo.com/get")
  }

  val transfomer = new HeaderTransformer(scala.collection.immutable.Seq(RawHeader("X-Foo", "123")))
  val headerPreFilter = new RequestTransformingPreFilter(transfomer)

  val routes: Route =
    path("foo") {
      get {
        val filterChain = new FilterChain(headerPreFilter, NoOpPostFilter)
        filterChain.apply(Upstreams.fooUpstream)
      }
    }

  /** Run the server **/
  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}