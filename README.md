# Switch

Switch is a library for writing customizable API Gateways using AKka HTTP.

## Getting started

### Request Filters

### Response Filters

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
