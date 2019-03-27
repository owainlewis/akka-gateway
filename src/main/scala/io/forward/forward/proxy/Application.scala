package io.forward.forward.proxy

object Application extends App {

  val proxy = new ReverseProxy()

  proxy.start("localhost", 8080)
}
