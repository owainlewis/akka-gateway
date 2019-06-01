package io.forward.gateway.aws

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive0}
import akka.stream.Materializer
import com.amazonaws.services.lambda.model.InvokeRequest

import scala.concurrent.{ExecutionContext, Future}

final class AWSLambdaAuthorizer(val region: String, val accessKey: String, val secretKey: String, functionName: String)(implicit system: ActorSystem, ctx: ExecutionContext, m: Materializer) extends AWSFunctionInvoker {
  def authorize: Directive0 = {
    optionalHeaderValueByName("Authorization") flatMap {
      case Some(token) =>
        onSuccess(invokeLambdaFunction(token)) flatMap { result =>
          if (result.status.intValue() == 200) {
            pass
          } else {
            reject(AuthorizationFailedRejection)
          }
        }
      case None => reject(AuthorizationFailedRejection)
    }
  }

  def invokeLambdaFunction(token: String): Future[HttpResponse] = {
      val lambdaResponse = client.invoke(new InvokeRequest()
        .withFunctionName(functionName)
        .withPayload(token))
      Future.successful(toHttpResponse(lambdaResponse))
  }
}