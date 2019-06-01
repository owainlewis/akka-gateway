package io.forward.gateway.auth

import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive, Directive0}
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future

final class AWSLambdaAuthorizer(region: String, accessKey: String, secretKey: String, functionName: String) {

  def authorize: Directive0 = {
    optionalHeaderValueByName("Authorization") flatMap {
      case Some(token) =>
        onSuccess(invokeLambdaFunction(token)) flatMap { result =>
          if (result) {
            pass
          } else {
            reject(AuthorizationFailedRejection)
          }
        }
      case None => reject(AuthorizationFailedRejection)
    }
  }

  private def invokeLambdaFunction(token: String): Future[Boolean] = {
    Future.successful(true)
  }
}
