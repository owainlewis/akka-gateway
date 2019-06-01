package io.forward.gateway.aws

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.amazonaws.services.lambda.model.InvokeRequest
import io.forward.gateway.model.Backend

import scala.concurrent.{ExecutionContext, Future}

/**
  * An AWS Backend supports forwarding on to an AWS Lambda function.
  *
  * @param region An AWS region name
  * @param accessKey An AWS accessKey
  * @param secretKey An AWS secretAccessKey
  * @param functionName The name of the function to invoke
  */
final class AWSLambdaBackend(val region: String, val accessKey: String, val secretKey: String, functionName: String)(implicit system: ActorSystem, ctx: ExecutionContext, m: Materializer) extends Backend with AWSFunctionInvoker {
  def apply(request: HttpRequest): Future[HttpResponse] = {
    Unmarshal(request.entity).to[String] map { payload =>
      val lambdaResponse = client.invoke(new InvokeRequest()
        .withFunctionName(functionName)
        .withPayload(payload))
      toHttpResponse(lambdaResponse)
    }
  }
}

object AWSLambdaBackend {
  def apply(region: String, accessKey: String, secretKey: String, functionName: String)(implicit system: ActorSystem, ctx: ExecutionContext, m: Materializer): AWSLambdaBackend =
    new AWSLambdaBackend(region, accessKey, secretKey, functionName)
}
