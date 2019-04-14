package io.forward.gateway.modules.loadbalance

import akka.http.scaladsl.model.Uri

trait LoadBalancer {
  def next(): Uri
}
