package io.forward.switch.filters

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.forward.switch.core.Backend

import scala.util.{Failure, Success}

/**
  * A filter chain encapsulates the logic for running pre and post filters before and after executing an
  * [[Backend]]
  *
  * TODO make this a sequence of filters
  *
  * @param preFilter
  * @param postFilter
  * @tparam T
  */
final class FilterChain[T](preFilter: PreFilter, postFilter: PostFilter[T]) {
  def apply(upstream: Backend): Route =
    extractRequest { request: HttpRequest =>
      onSuccess(preFilter.apply(request)) {
        case Left(response) => complete(response)
        case Right(r) => dispatch(upstream, r)
      }
    }

  private def dispatch(upstream: Backend, request: HttpRequest) =
    onComplete(upstream.apply(request)) {
      case Success(response) => complete(response)
      case Failure(_) => complete("Internal server error")
    }
}

object FilterChain {
  def apply[T](preFilter: PreFilter, postFilter: PostFilter[T]) =
    new FilterChain[T](preFilter, postFilter)
}
