package io.forward.switch

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.{get, path}
import akka.http.scaladsl.server.Route
import io.forward.switch.core.HttpUpstream
import io.forward.switch.filters.{NoOpPostFilter, RequestTransformingPreFilter}
import io.forward.switch.modules.transform.HeaderTransformer

object Application extends App {

  import DefaultImplicits._

  object Upstreams {
    val fooUpstream = new HttpUpstream("https://postman-echo.com/get")
  }

  val transfomer = new HeaderTransformer(scala.collection.immutable.Seq(RawHeader("X-Foo", "123")))
  val headerPreFilter = new RequestTransformingPreFilter(transfomer)

  val routes: Route =
    path("foo") {
      get {
        println("Request")
        val filterChain = new FilterChain(headerPreFilter, NoOpPostFilter)
        filterChain.apply(Upstreams.fooUpstream)
      }
    }

  /** Run the server **/
  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}