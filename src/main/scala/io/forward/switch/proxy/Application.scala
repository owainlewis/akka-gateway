package io.forward.switch.proxy

object Application extends App {
  new ReverseProxy().start("localhost", 8080)
}
