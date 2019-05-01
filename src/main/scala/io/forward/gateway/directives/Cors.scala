package io.forward.gateway.directives

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.{RawHeader, _}
import akka.http.scaladsl.model.{HttpHeader, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * Add CORS to requests using this directive
  *
  * @param allowOrigin specifies which domain/origin is allowed to access a resource.
  *                    A wildcard '*' may be used to allow all but this is only recommended for public APIs
  * @param allowMethods a comma separated list of HTTP methods that are allowed to access a resource. These are used in response to a pre-flight request.
  *                       The default of Access-Control-Allow-Methods is to allow through all simple methods, even on pre-flight requests
  * @param allowHeaders a comma separated list of allowed HTTP headers
  * @param exposeHeaders a comma separated list of HTTP headers the browser is allowed to access
  * @param allowCredentials must be set to true if requests are made with credentials
  * @param maxAge indicates how long (in delta-seconds) the results of a pre-flight request can be cached by a browser
  */
case class CorsConfiguration(allowOrigin: Option[String] = None,
                             allowMethods: Option[Seq[String]] = None,
                             allowHeaders: Option[Seq[String]] = None,
                             exposeHeaders: Option[Seq[String]] = None,
                             allowCredentials: Option[Boolean] = None,
                             maxAge: Option[Int] = None) {

  def withAllowOrigin(origin: String): CorsConfiguration = this.copy(allowOrigin = Some(origin))
  def withAllowMethods(methods: String*): CorsConfiguration = this.copy(allowMethods = Some(methods))
  def withAllowHeaders(headers: String*): CorsConfiguration = this.copy(allowHeaders = Some(headers))
  def withExposeHeaders(headers: String*): CorsConfiguration = this.copy(exposeHeaders = Some(headers))
  def withAllowCredentials(allow: Boolean): CorsConfiguration = this.copy(allowCredentials = Some(allow))
  def withMaxAge(age: Int): CorsConfiguration = this.copy(maxAge = Some(age))
}

class CorsHandler(configuration: CorsConfiguration) {

  def withCors(r: Route): Route = respondWithHeaders(accessControlHeaders) {
    preFlightRequestHandler ~ r
  }

  private def accessControlHeaders: List[HttpHeader] = List(
    buildHeader(`Access-Control-Allow-Origin`.name, configuration.allowOrigin),
    buildHeader(`Access-Control-Allow-Methods`.name, configuration.allowMethods.map(_.mkString(","))),
    buildHeader(`Access-Control-Allow-Headers`.name, configuration.allowHeaders.map(_.mkString(","))),
    buildHeader(`Access-Control-Expose-Headers`.name, configuration.exposeHeaders.map(_.mkString(","))),
    buildHeader(`Access-Control-Allow-Credentials`.name, configuration.allowCredentials.map(_.toString)),
    buildHeader(`Access-Control-Max-Age`.name, configuration.maxAge.map(_.toString))
  ).flatten

  private def buildHeader[A](name: String, value: Option[String]): Option[RawHeader] =
    value.map(v => RawHeader(name, v))

  private def preFlightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }
}
