package io.forward.gateway.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

trait PreFilter {
  /**
    * Apply a pre-filter to a [[HttpRequest]]. If a [[HttpResponse]] is returned
    * then that is immediately passed back to the caller and not sent upstream.
    *
    * If a [[HttpRequest]] is returned it will be sent upstream or to another [[PreFilter]] for further
    * processing.
    *
    * @param request An incoming [[HttpRequest]] destined for an upstream
    *
    * @return Either a [[HttpResponse]] or a [[HttpRequest]]
    */
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]]
  /**
    * Abort a request chain by immediately exiting and returning a response.
    *
    * For example, you might want to authenticate a request and return immediately if authentication fails.
    * Another example would be validating a request where an invalid request should not be sent upstream.
    *
    * @param response The [[akka.http.scaladsl.model.HttpResponse]] to return
    *
    * @return Either a [[HttpResponse]] or a [[HttpRequest]]
    */
  def abort(response: HttpResponse): Future[Either[HttpResponse, HttpRequest]] = Future.successful(Left(response))
  /**
    * Calling continue will proceed to the next step of the filter chain
    *
    * @param request An [[HttpRequest]]
    *
    * @return Either a [[HttpResponse]] or a [[HttpRequest]]
    */
  def continue(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = Future.successful(Right(request))
}

object NoOpPreFilter extends PreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    continue(request)
}