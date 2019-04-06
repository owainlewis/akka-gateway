package examples

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.forward.switch.core.HttpBackend
import io.forward.switch.filters.{FilterChain, NoOpPostFilter, RequestTransformingPreFilter}
import io.forward.switch.modules.transform.HeaderTransformer

import scala.concurrent.ExecutionContext

object SimpleGateway extends App with DefaultImplicits {

  object FooBackend {
    private val headerTransform = HeaderTransformer(add = Seq(RawHeader("X-Foo", "123")), remove = Seq("X-Bar"))
    private val headerPreFilter = RequestTransformingPreFilter(headerTransform)

    val route: Route = FilterChain(headerPreFilter, NoOpPostFilter)
      .apply(HttpBackend("https://postman-echo.com/get"))
  }

  val routes: Route =
    path("foo") {
      get {
        FooBackend.route
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