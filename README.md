# Gateway

Gateway is a library for writing bespoke API Gateways using Akka HTTP. It follows a very simple request response pipeline flow.
You can add request and response filters to modify incoming requests, perform validation, transform responses, perform authentication and more.

## Running the examples

```
sbt "project examples" "run
```

## Supported Backends

- HTTP
- AWS Lambda

## Getting started

The example below shows how to create a custom API Gateway that dispatches request to a HTTP backend and AWS Lambda function. We apply request filters to modify inbound requests before they are sent upstream. 

```scala
object SimpleGateway extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  import io.forward.gateway.directives.Filter._
  import io.forward.gateway.directives.Proxy._

  val headerFilter = RemoveHeaders(Seq("Authorization"))

  val route = pathSingleSlash {
    // HTTP example
    get {
      withRequestFilters(headerFilter) {
        proxy(new HttpBackend("https://postman-echo.com/get"))
      }
    }
    // Lambda function example
  } ~ path("v1") {
    post {
      proxy(new AWSLambdaBackend(Regions.EU_WEST_1.getName, System.getenv("AWS_KEY"), System.getenv("AWS_SECRET"), "helloFunction"))
    }
  }

  val service = Gateway(route)

  service.start("localhost", 8080)
}
```

### Request Filters

A request filter can be used to modify an incoming HTTP request before it is sent upstream. It can also perform logic such as
authentication.

```scala
trait RequestFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]]
```

### Response Filters

A response filter is used to modify the HTTP response received from a Backend. For example you might want to modify headers or response messages.

```scala
trait ResponseFilter {
  def apply(request: HttpRequest, response: HttpResponse, data: RequestData): Future[HttpResponse]
}
```

### Writing custom filters

It's easy to extend this project and add additional filters. Here's a simple request filter that adds HTTP headers to all requests

```scala
final class AddHeaders(headers: HttpHeader*) extends RequestFilter {
  def onRequest(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]] = {
    val requestWithAdditionalHeaders = headers.foldLeft(request)((r,v) => r.addHeader(v))
    continue(requestWithAdditionalHeaders)
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
