package io.forward.switch.modules.transform

import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import scala.collection.immutable.{ Seq => ISeq }

sealed trait RequestTransformer {
  def transform(httpRequest: HttpRequest): HttpRequest
}

final class HeaderTransformer(add: ISeq[HttpHeader], delete: Seq[String] = Seq.empty) extends RequestTransformer {

  def addHeaders(request: HttpRequest): HttpRequest = request.withHeaders(add)

  def deleteHeaders(request: HttpRequest): HttpRequest = request

  def transform(httpRequest: HttpRequest): HttpRequest = {
    val transformer = deleteHeaders _ andThen addHeaders
    transformer(httpRequest)
  }
}

object HeaderTransformer {
  def apply(add: ISeq[HttpHeader], delete: Seq[String] = Seq.empty): HeaderTransformer =
    new HeaderTransformer(add, delete)
}
