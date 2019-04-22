package io.forward.gateway.model

import akka.http.scaladsl.model.StatusCodes

object Errors {

  sealed trait GatewayError { val statusCode: Int; val data: String }

  case class NotFound(data: String) extends GatewayError {
    val statusCode: Int = StatusCodes.NotFound.intValue
  }

  case class BadRequest(data: String) extends GatewayError {
    val statusCode: Int = StatusCodes.BadRequest.intValue
  }
}
