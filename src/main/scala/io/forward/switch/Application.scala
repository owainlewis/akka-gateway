package io.forward.switch

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken, RawHeader}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Route}
import io.forward.switch.core.HttpUpstream
import io.forward.switch.filters.{NoOpPostFilter, RequestTransformingPreFilter}
import io.forward.switch.modules.auth0.JWTPreFilter
import io.forward.switch.modules.transform.HeaderTransformer

object Application extends App {
  import DefaultImplicits._

  val transfomer = new HeaderTransformer(scala.collection.immutable.Seq(RawHeader("X-Foo", "123")))
  val headerPreFilter = new JWTPreFilter()

  val routes: Route =
    path("foo") {
      get {
        FilterChain(headerPreFilter, NoOpPostFilter)
          .apply(HttpUpstream("https://postman-echo.com/get"))
      }
    }

  /** Run the server **/
  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}