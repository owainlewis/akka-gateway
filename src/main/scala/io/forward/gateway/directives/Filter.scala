package io.forward.gateway.directives

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.{Directive, Directive0, Directive1}
import io.forward.gateway.filters.{PostFilter, PreFilter}
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

object Filter {
  /**
    * Using the withPreFilter directive you can compose prefilters using the & operator
    *
    * val f1 = withPreFilter(a)
    * val f2 = withPreFilter(b)
    *
    * val f3 = f1 & f2
    *
    * @param filter A [[io.forward.gateway.filters.PreFilter]] to apply to a request
    * @return
    */
  def withPreFilter(filter: PreFilter): Directive0 =
    applyRequestFilter(filter).flatMap {
      case Left(response) => complete(response)
      case Right(request) => mapRequest(_ => request)
    }

  /**
    * Apply a post filter to a response
    *
    * @param filter A [[PostFilter]]
    * @return
    */
  def withPostFilter(filter: PostFilter): Directive0 = {
    complete("OK")
  }

  private def applyRequestFilter(filter: PreFilter): Directive1[Either[HttpResponse, HttpRequest]] = {
    Directive { inner => ctx =>
      implicit val ex: ExecutionContext = ctx.executionContext
      filter.onRequest(ctx.request).flatMap { result =>
        inner(Tuple1(result))(ctx)
      }
    }
  }
}