# Switch

Switch is a library for writing bespoke API Gateways using AKka HTTP. It supports

## Running the examples

```
sbt "project examples" "run
```

## Features

#### Core
  - Rate limiting
  - Fan out request patterns
  - Validation
  - Response transformation
  - Load Balancing
  - Websockets

#### Authentication
  - Auth0

#### Cloud Provider
  - AWS
    - Lambda
    - S3
  - Google Functions

## Concepts

Switch is built around some simple concepts.

HTTPRequest -> PreFilter[HttpRequest] -> (Return || Dispatch Upstream) -> PostFilter[HttpResponse] -> HttpResponse

## Getting started

The example below shows how to create a custom API Gateway using Switch. We add some pre filters to modify incoming
requests before sending them upstream.

```scala
object SimpleGateway extends App with DefaultImplicits {

  object FooBackend {
    private val firstPreFilter = RequestTransformingPreFilter(HeaderTransformer(add = Seq(RawHeader("X-Foo", "123"))))
    private val secondPreFilter = RateLimitingPreFilter(10)

    private val headerPreFilter = firstPreFilter ~> secondPreFilter

    val route: Route = FilterChain(headerPreFilter, NoOpPostFilter)
      .apply(HttpBackend("https://postman-echo.com/get"))
  }

  val routes: Route =
    path("foo") {
      get {
        FooBackend.route
      }
    }

  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}
```

### Request Pre Filters

A pre filter can be used to modify an incoming HTTP request before it is sent upstream. It can also perform logic such as
authentication.

```scala
trait PreFilter {
  def apply(request: HttpRequest): Future[Either[HttpResponse, HttpRequest]]
```

### Response Post Filters

A post filter is used to modify the HTTP response received from a Backend. For example you might want to modify headers or response messages.

```scala
trait PostFilter[-RequestData] {
  def apply(request: HttpRequest, response: HttpResponse, data: RequestData): Future[HttpResponse]
}
```

### Writing custom filters

TODO
