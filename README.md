# Gateway

Gateway is a library for writing bespoke API Gateways using Akkaa HTTP. It follows a very simple request response pipeline flow.
You can add request and response filters to modify incoming requests, perform validation, transform responses, perform authentication and more.

## Running the examples

```
sbt "project examples" "run
```

## Supported Backends

- HTTP
- AWS Lambda

## Getting started

The example below shows how to create a custom API Gateway using Switch. We add some pre filters to modify incoming
requests before sending them upstream.

```scala
object SimpleGateway extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  import io.forward.gateway.directives.Filter._
  import io.forward.gateway.directives.Proxy._

  val backend = new HttpBackend("https://postman-echo.com/get")

  val headerFilter = RemoveHeaders(Seq("Authorization"))

  val route = pathSingleSlash {
    get {
      withRequestFilters(headerFilter) {
        proxy(backend)
      }
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
