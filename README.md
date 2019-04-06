# Switch

Switch is a library for writing bespoke API Gateways using AKka HTTP. It supports

- Core
  - Rate limiting
  - Authentication
  - Fan out request patterns
  - Validation
  - Response transformation
  - Load Balancing

- Cloud Provider
  - AWS
    - Lambda
    - S3
  - Google Functions

## Concepts

Switch is built around some simple concepts.

HTTPRequest -> PreFilter[HttpRequest] -> Dispatch Upstream -> PostFilter -> HttpResponse

## Getting started

```scala
object Application extends App {
  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val http = Http(system)

  object Upstreams {
    val fooUpstream = new HttpUpstream("https://hookb.in/XkLMbV8x0YubobmZEm1m")
    val barUpstream = new HttpUpstream("https://jsonplaceholder.typicode.com/todos/1")
  }

  val routes: Route =
    path("foo") {
      get {
          val filterChain = new FilterChain(NoOpPreFilter, NoOpPostFilter)
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
