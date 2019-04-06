# Switch

Switch is a library for writing bespoke API Gateways using AKka HTTP. It supports

## Core
  - Rate limiting
  - Fan out request patterns
  - Validation
  - Response transformation
  - Load Balancing
  - Websockets

## Authentication
  - Auth0

## Cloud Provider
  - AWS
    - Lambda
    - S3
  - Google Functions

## Concepts

Switch is built around some simple concepts.

HTTPRequest -> PreFilter[HttpRequest] -> (Return || Dispatch Upstream) -> PostFilter[HttpResponse] -> HttpResponse

## Getting started

```scala
object SimpleGateway extends App with DefaultImplicits {

  object FooBackend {
    private val headerTransform = HeaderTransformer(add = Seq(RawHeader("X-Foo", "123")), remove = Seq("X-Bar"))
    private val headerPreFilter = RequestTransformingPreFilter(headerTransform)

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

### Response Post Filters
