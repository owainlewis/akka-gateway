package io.forward.gateway.aws

import akka.http.scaladsl.model._
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.lambda.{AWSLambda, AWSLambdaClientBuilder}
import com.amazonaws.services.lambda.model.{InvokeRequest, InvokeResult}
import io.forward.gateway.model.Backend
import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer

import scala.concurrent.{ExecutionContext, Future}

/**
  * An AWS Backend supports forwarding on to an AWS Lambda function.
  *
  * @param region An AWS region name
  * @param accessKey An AWS accessKey
  * @param secretKey An AWS secretAccessKey
  * @param functionName The name of the function to invoke
  */
final class AWSLambdaBackend(region: String, accessKey: String, secretKey: String, functionName: String)(implicit system: ActorSystem, ctx: ExecutionContext, m: Materializer) extends Backend {
  def apply(request: HttpRequest): Future[HttpResponse] = {
    val client = buildAWSClient()
    Unmarshal(request.entity).to[String] map { payload =>
      val lambdaResponse = client.invoke(new InvokeRequest()
        .withFunctionName("helloFunction")
        .withPayload(payload))
      toHttpResponse(lambdaResponse)
    }
  }

  private def toHttpResponse(result: InvokeResult): HttpResponse = {
    val payload = StandardCharsets.UTF_8.decode(result.getPayload).toString
    HttpResponse(StatusCode.int2StatusCode(result.getStatusCode), entity = HttpEntity(payload))
  }

  private def buildAWSClient(): AWSLambda =
    AWSLambdaClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
      .withRegion(region)
      .build()
}

object AWSLambdaBackend {
  def apply(region: String, accessKey: String, secretKey: String, functionName: String)(implicit system: ActorSystem, ctx: ExecutionContext, m: Materializer): AWSLambdaBackend =
    new AWSLambdaBackend(region, accessKey, secretKey, functionName)
}
