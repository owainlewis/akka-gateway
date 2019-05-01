package io.forward.gateway.directives

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.{Directive, Directive0, Directive1}
import akka.http.scaladsl.server.Directives._
import io.forward.gateway.model.{ResponseFilter, RequestFilter}

import scala.concurrent.ExecutionContext

object Filter {
  /**
    * Takes multiple filters, lifts and then combines the into one
    *
    * @param filters A sequence of request filters to compose together
    * @return A routing [[Directive]]
    */
  def withRequestFilters(filters: RequestFilter*): Directive0 = filters.toSeq.map(withRequestFilter).reduce(_ & _)

  /**
    * Using the withPreFilter directive you can compose prefilters using the & operator
    *
    * val f1 = withRequestFilter(a)
    * val f2 = withRequestFilter(b)
    *
    * val f3 = f1 & f2
    *
    * @param filter a [[RequestFilter]] to apply to a request
    * @return
    */
  def withRequestFilter(filter: RequestFilter): Directive0 =
    applyRequestFilter(filter).flatMap {
      case Left(response) => complete(response)
      case Right(request) => mapRequest(_ => request)
    }

  /**
    * Apply a [[ResponseFilter]] to A [[HttpResponse]]
    *
    * @param filter a [[ResponseFilter]]
    * @return
    */
  def withResponseFilter(filter: ResponseFilter): Directive0 = {
    complete("OK")
  }

  private def applyRequestFilter(filter: RequestFilter): Directive1[Either[HttpResponse, HttpRequest]] = {
    Directive { inner => ctx =>
      implicit val ex: ExecutionContext = ctx.executionContext
      filter.onRequest(ctx.request).flatMap { result =>
        inner(Tuple1(result))(ctx)
      }
    }
  }
}