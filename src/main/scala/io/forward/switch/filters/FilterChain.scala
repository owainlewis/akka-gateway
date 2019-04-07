package io.forward.switch.filters

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.forward.switch.core.Backend
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * A filter chain encapsulates the logic for running pre and post filters before and after executing an
  * [[Backend]]
  *
  * TODO make this a sequence of filters
  *
  * @param preFilter
  * @param postFilter
  * @tparam T
  */
final class FilterChain(pre: PreFilter, backend: Backend, post: PostFilter)(implicit ex: ExecutionContext, mat: Materializer){
  def apply(): Route =
    extractRequest { request: HttpRequest =>
      onSuccess(pre.apply(request)) {
        case Left(response) => complete(response)
        case Right(r) => dispatch(backend, post, r)
      }
    }

  private def dispatch(upstream: Backend, post: PostFilter, request: HttpRequest) =
    onSuccess(upstream.apply(request)) { response =>
      // TODO tidy this up ! : )
      val fb = response.entity.withoutSizeLimit().toStrict(5.seconds).flatMap(Unmarshal(_).to[String])
      val x = fb flatMap (body => post.apply(response, body))
      onSuccess(x) { result =>
        complete(result)
      }
    }
}

object FilterChain {
  def apply(pre: PreFilter, backend: Backend, post: PostFilter)(implicit ex: ExecutionContext, mat: Materializer) =
    new FilterChain(pre, backend, post)
}
