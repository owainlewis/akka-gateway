package io.forward.gateway.modules.loadbalance

import java.util.concurrent.atomic.AtomicInteger

import akka.http.scaladsl.model.Uri

final class RoundRobin(values: Seq[Uri]) extends LoadBalancer {
  private val counter = new AtomicInteger(0)

  def next(): Uri = {
    val index = counter.incrementAndGet() % (if (values.nonEmpty) values.size else 1)
    values.apply(index)
  }
}
