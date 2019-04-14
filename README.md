# Gateway

Gateway is a library for writing bespoke API Gateways using Akka HTTP.

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

Gateway is built around some simple concepts.

HTTPRequest -> PreFilter[HttpRequest] -> (Return || Dispatch Upstream) -> PostFilter[HttpResponse] -> HttpResponse

## Getting started

The example below shows how to create a custom API Gateway using Switch. We add some pre filters to modify incoming
requests before sending them upstream.

```scala
object SimpleGateway extends App with DefaultImplicits {

  val gateway: Route =
    path("foo") {
      get {
        FilterChain(BasicAuthPreFilter("password"), HttpBackend("https://postman-echo.com/get"), NoOpPostFilter)
      }
    }

  Http().bindAndHandle(gateway, interface = "localhost", port = 8080)
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
