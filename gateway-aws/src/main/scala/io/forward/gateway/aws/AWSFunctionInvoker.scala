package io.forward.gateway.aws

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.model._
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.lambda.model.InvokeResult
import com.amazonaws.services.lambda.{AWSLambda, AWSLambdaClientBuilder}

trait AWSFunctionInvoker {
  val accessKey: String
  val secretKey: String
  val region: String

  val client: AWSLambda = buildAWSClient

  def toHttpResponse(result: InvokeResult): HttpResponse = {
    val payload = StandardCharsets.UTF_8.decode(result.getPayload).toString
    HttpResponse(StatusCode.int2StatusCode(result.getStatusCode), entity = HttpEntity(payload))
  }

  private def buildAWSClient: AWSLambda =
    AWSLambdaClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
      .withRegion(region)
      .build()
}
