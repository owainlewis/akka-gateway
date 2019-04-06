# Switch

Switch is a library for writing bespoke API Gateways using AKka HTTP. It supports

- Core
  - Rate limiting
  - Authentication
  - Fan out request patterns
  - Validation
  - Response transformation
  - Load Balancing
  - Websockets

- Cloud Provider
  - AWS
    - Lambda
    - S3
  - Google Functions

## Concepts

Switch is built around some simple concepts.

HTTPRequest -> PreFilter[HttpRequest] -> (Return || Dispatch Upstream) -> PostFilter[HttpResponse] -> HttpResponse

## Getting started

```scala
object Application extends App {
  import DefaultImplicits._
  
  object Upstreams {
    val fooUpstream = new HttpUpstream("https://postman-echo.com/get")
  }

  val transfomer = new HeaderTransformer(scala.collection.immutable.Seq(RawHeader("X-Foo", "123")))
  val headerPreFilter = new RequestTransformingPreFilter(transfomer)

  val routes: Route =
    path("foo") {
      get {
        println("Request")
        val filterChain = new FilterChain(headerPreFilter, NoOpPostFilter)
        filterChain.apply(Upstreams.fooUpstream)
      }
    }

  /** Run the server **/
  Http().bindAndHandle(routes, interface = "localhost", port = 8080)
}

```

### Request Pre Filters

A pre filter can be used to modify an incoming HTTP request before it is sent upstream. It can also perform logic such as
authentication.

### Response Post Filters
