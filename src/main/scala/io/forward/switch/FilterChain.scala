package io.forward.switch

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.forward.switch.core.Upstream

import scala.util.{Failure, Success}
import scala.concurrent.Future

trait PreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]]
  /**
    * Halt a request chain by immediately existing and returning a response
    *
    * @param response The [[akka.http.scaladsl.model.HttpResponse]] to return
    *
    * @return
    */
  def exit(response: HttpResponse): Future[Either[HttpResponse, HttpRequest]] = Future.successful(Left(response))
}

object NoOpPreFilter extends PreFilter {
  def apply(request: HttpRequest) =
    Future.successful(Left(HttpResponse(status = StatusCodes.Created)))
}

trait PostFilter[-RequestData] {
  def apply(request: HttpRequest, response: HttpResponse, data: RequestData): Future[HttpResponse]
}

object NoOpPostFilter extends PostFilter[Unit] {
  override def apply(request: HttpRequest, response: HttpResponse, data: Unit): Future[HttpResponse] =
    Future.successful(HttpResponse(status = StatusCodes.BadGateway))
}


/**
  * A filter chain encapsulates the logic for running pre and post filters before and after executing an
  * [[Upstream]]
  *
  * TODO make this a sequence of filters
  *
  * @param preFilter
  * @param postFilter
  * @tparam T
  */
final class FilterChain[T](preFilter: PreFilter, postFilter: PostFilter[T]) {
  def apply(upstream: Upstream): Route =
    extractRequest { request: HttpRequest =>
      onComplete(preFilter.apply(request)) {
        case Success(result) =>
          result match {
            case Left(response) => complete(response)
            case Right(r) => dispatch(upstream, r)
          }
      }
    }

  private def dispatch(upstream: Upstream, request: HttpRequest) =
    onComplete(upstream.apply(request)) {
      case Success(response) => complete(response)
      case Failure(_) => complete("Internal server error")
    }
}
