package io.forward.gateway.aws

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.`Authorization`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive0}
import akka.stream.Materializer
import com.amazonaws.services.lambda.model.InvokeRequest
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}

sealed case class LambdaAuthBody(token: String)

final class AWSLambdaAuthorizer(val region: String, val accessKey: String, val secretKey: String, functionName: String)(implicit system: ActorSystem, ctx: ExecutionContext, m: Materializer) extends AWSFunctionInvoker {
  def authorize: Directive0 = {
    system.log.info("Authorizing request with AWS")
    optionalHeaderValueByName(`Authorization`.name) flatMap {
      case Some(token) =>
        onSuccess(invokeLambdaFunction(token)) flatMap { _ =>
          // TODO check whatever makes sense with AWD lambda auth functions
          pass
        }
      case None => reject(AuthorizationFailedRejection)
    }
  }

  // TODO non blocking IO
  def invokeLambdaFunction(token: String): Future[HttpResponse] = {
      val lambdaResponse = client.invoke(new InvokeRequest()
        .withFunctionName(functionName)
        .withPayload(LambdaAuthBody(token).asJson.toString))
      Future.successful(toHttpResponse(lambdaResponse))
  }
}