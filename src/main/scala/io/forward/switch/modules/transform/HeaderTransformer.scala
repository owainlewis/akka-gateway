package io.forward.switch.modules.transform

import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import io.forward.switch.filters.pre.transform.RequestTransformer

/**
  * A header transformer can be used to modify HTTP headers on a request.
  *
  * It can either add or remove HTTP headers
  *
  * @param add A [[Seq]] of [[HttpHeader]]'s to add to a request
  * @param remove A [[Seq]] of [[String]] to remove from a request
  */
final class HeaderTransformer(add: Seq[HttpHeader], remove: Seq[String] = Seq.empty) extends RequestTransformer {
  /**
    * Transform an HTTP request by modifying it's headers
    *
    * @param httpRequest A [[HttpRequest]] to modify
    * @return A [[HttpRequest]]
    */
  def transform(request: HttpRequest): HttpRequest =
    deleteThenAdd(request)

  private val deleteThenAdd = deleteHeaders _ andThen addHeaders

  private def addHeaders(request: HttpRequest) =
    add.foldLeft(request)((x, y) => x.addHeader(y))

  private def deleteHeaders(request: HttpRequest) =
    remove.foldLeft(request)((x,y) => x.removeHeader(y))
}

object HeaderTransformer {
  def apply(add: Seq[HttpHeader], remove: Seq[String] = Seq.empty): HeaderTransformer =
    new HeaderTransformer(add, remove)
}
