# Gateway

Gateway is a library for writing bespoke API Gateways using Akka HTTP. It follows a very simple request response pipeline flow.
You can add request and response filters to modify incoming requests, perform validation, transform responses, perform authentication and more.

## Running the examples

```
sbt "project examples" "run
```

## Supported Backends

- HTTP :heavy_check_mark:
- AWS
  * Lambda :heavy_check_mark:
  * S3
- Google
  * Cloud Functions
- Azure
- Oracle Cloud Infrastructure

## Supported Features

- CORS
- Authentication
- Validation

## Getting started

The example below shows how to create a custom API Gateway that dispatches request to a HTTP backend and AWS Lambda function. We apply request filters to modify inbound requests before they are sent upstream. 

```scala
object SimpleGateway extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  import io.forward.gateway.directives.Proxy._
  import io.forward.gateway.directives._

  val removeHeaders = new RemoveHeadersRequestFilter("Authorization").lift

  val corsConfiguration = CorsConfiguration().withAllowOrigin("*").withAllowMethods("GET", "PUT")

  val route = pathSingleSlash {
    new CorsHandler(corsConfiguration).withCors {
      // HTTP example
      get {
        removeHeaders {
          proxy(new HttpBackend("https://postman-echo.com/get"))
        }
      }
    }
    // Lambda function example
  } ~ path("v1") {
    post {
      proxy(new AWSLambdaBackend(Regions.EU_WEST_1.getName, System.getenv("AWS_KEY"), System.getenv("AWS_SECRET"), "helloFunction"))
    }
  }

  Gateway(route).start("localhost", 8080)
}
```

### Request Filters

A request filter can be used to modify an incoming HTTP request before it is sent upstream. It can also perform logic such as
authentication.

```scala
trait RequestFilter {
  def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]]
}
```

### Response Filters

A response filter is used to modify the HTTP response received from a Backend. For example you might want to modify headers or response message before returning to a user.

```scala
trait ResponseFilter {
  def onResponse(response: HttpResponse): Future[HttpResponse]
}
```

### Writing custom filters

It's easy to extend this project and add additional filters. Here's a simple request filter that adds HTTP headers to all requests

```scala
final class AddHeaders(headers: HttpHeader*) extends RequestFilter {
  def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    // Add headers before request is dispatched to backend
    continue(headers.foldLeft(request)((r,v) => r.addHeader(v)))
  }
}
```

We can use that in our gateway as follows

```scala
val route =
  get {
    withRequestFilters(new AddHeaders(RawHeader("X-Foo", "Bar"))) {
      proxy(new HttpBackend("https://postman-echo.com/get"))
    }
  }
```
