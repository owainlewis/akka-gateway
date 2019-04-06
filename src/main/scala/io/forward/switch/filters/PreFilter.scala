package io.forward.switch.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import io.forward.switch.modules.transform.RequestTransformer

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
    * @return Either a HttpResponse or a HttpRequest
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
    * @return
    */
  def abort(response: HttpResponse): Future[Either[HttpResponse, HttpRequest]] = Future.successful(Left(response))
}

object NoOpPreFilter extends PreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] =
    Future.successful(Left(HttpResponse(status = StatusCodes.OK)))
}

object HeaderAuthenticatingPreFilter extends PreFilter {
  override def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    val notAuthorized = Left(HttpResponse(status = StatusCodes.Unauthorized))
    Future.successful(notAuthorized)
  }
}

class RequestTransformingPreFilter(transformer: RequestTransformer) extends PreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    Future.successful(Right(transformer.transform(request)))
  }
}