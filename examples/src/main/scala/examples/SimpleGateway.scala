package examples

import akka.actor.ActorSystem
import io.forward.gateway.Gateway
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import io.forward.gateway.proxy.Client
import akka.http.scaladsl.model.StatusCodes._
import io.forward.gateway.filters.NoOpPreFilter

import scala.concurrent.ExecutionContext

object SimpleGateway extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  import io.forward.gateway.filters.FilterDirectives._

  val client = new Client("http://dummy.restapiexample.com/api/v1/employees")

  val route = pathSingleSlash {
    get {
      withPreFilter(NoOpPreFilter) {
        withPreFilter(NoOpPreFilter) {
          complete(OK -> "<h1>Get request with akka-http</h1>")
        }
      }
    }
  }

  val service = Gateway(route)

  service.start("localhost", 8080)
}
