# Switch

Switch is a library for writing customizable API Gateways using AKka HTTP.

## Getting started

### Request Filters

### Response Filters

```scala
object Application extends App with ReverseProxy {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val http = Http(system)

  object Upstreams {
    val fooUpstream = new HttpUpstream("https://jsonplaceholder.typicode.com/todos")
    val barUpstream = new HttpUpstream("https://jsonplaceholder.typicode.com/todos/1")
  }

  val gateway: Route =
    path("foo") {
      get {
        extractRequestContext { ctx =>
          ctx.log.info("Proxying to Foo upstream")
          proxyTo(Upstreams.fooUpstream)
        }
      }
    } ~ path("bar") {
      get {
        proxyTo(Upstreams.barUpstream)
      }
    }

  /** Run the server **/
  Http().bindAndHandle(gateway, interface = "localhost", port = 8080)
}
```
