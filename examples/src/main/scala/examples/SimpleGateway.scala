package examples

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.amazonaws.regions.Regions
import io.forward.gateway.Gateway
import io.forward.gateway.aws.AWSLambdaBackend
import io.forward.gateway.core.backend.HttpBackend
import io.forward.gateway.filters.request._

import scala.concurrent.ExecutionContext

object SimpleGateway extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  import io.forward.gateway.directives.Filter._
  import io.forward.gateway.directives.Proxy._
  import io.forward.gateway.directives._

  val requestFilter = new RemoveHeadersRequestFilter("Authorization")

  val corsConfiguration = CorsConfiguration().withAllowOrigin("*").withAllowMethods("GET", "PUT")

  val route = pathSingleSlash {
    new CorsHandler(corsConfiguration).withCors {
      // HTTP example
      get {
        withRequestFilters(requestFilter) {
          proxy(new HttpBackend("https://postman-echo.com/get"))
        }
      }
    }
    // Lambda function example
  } ~ path("v1") {
    post {
      proxy(new AWSLambdaBackend(Regions.EU_WEST_1.getName, System.getenv("AWS_KEY"), System.getenv("AWS_SECRET"), "helloFunction"))
    }
  }

  val service = Gateway(route)

  service.start("localhost", 8080)
}
