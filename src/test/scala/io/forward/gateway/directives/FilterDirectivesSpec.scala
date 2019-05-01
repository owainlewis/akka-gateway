package io.forward.gateway.directives

import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.forward.gateway.model.RequestFilter
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future

class FilterDirectivesSpec extends FlatSpec with Matchers with ScalatestRouteTest {
  "A request filter" should "modify the request when a request is returned" in {
    val filter = new RequestFilter {
      def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
        Future.successful(Right(HttpRequest(HttpMethods.GET)))
      }
    }

    val route = Filter.withRequestFilter(filter) {
      extractRequest { request =>
        complete {
          request should equal(HttpRequest(HttpMethods.GET))
          HttpResponse(StatusCodes.Created)
        }
      }
    }

    Get("/") ~> route ~> check {
      response.status should equal(StatusCodes.Created)
    }
  }

  "A request filter" should "abort and return a response when a response is returned" in {
    val filter = new RequestFilter {
      def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
        Future.successful(Left(HttpResponse(StatusCodes.BadGateway)))
      }
    }

    val route = Filter.withRequestFilter(filter) {
      extractRequest { request =>
        complete {
          request should equal(request)
          HttpResponse(StatusCodes.OK)
        }
      }
    }

    Get("/") ~> route ~> check {
      response.status should equal(StatusCodes.BadGateway)
    }
  }
}
