package io.forward.gateway.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.forward.gateway.core.backend.Backend
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * A filter chain encapsulates the logic for running pre and post filters before and after executing an
  * [[Backend]]
  *
  * @param pre     A [[PreFilter]] to modify an incoming [[HttpRequest]]
  * @param backend A [[io.forward.gateway.core.backend.HttpBackend]] to proxy
  * @param post    A [[PostFilter]] to post process a [[HttpResponse]]
  * @tparam T
  */
final class FilterChain(pre: PreFilter, backend: Backend, post: PostFilter)(implicit ex: ExecutionContext, mat: Materializer){
  def asRoute(): Route =
    extractRequest { request: HttpRequest =>
      onSuccess(pre.apply(request)) {
        case Left(response) => complete(response)
        case Right(r) => dispatch(backend, post, r)
      }
    }

  private def dispatch(upstream: Backend, post: PostFilter, request: HttpRequest): Route =
    onSuccess(upstream.apply(request)) { response =>
      val postFilteredResponse = unmarshallResponseEntity(response) flatMap (post.apply(response, _))
      onSuccess(postFilteredResponse) (complete(_))
    }

  private def unmarshallResponseEntity[T](response: HttpResponse): Future[String] =
    response.entity.withoutSizeLimit().toStrict(5.seconds).flatMap(Unmarshal(_).to[String])
}

object FilterChain {
  def apply(pre: PreFilter, backend: Backend, post: PostFilter)(implicit ex: ExecutionContext, mat: Materializer): Route =
    new FilterChain(pre, backend, post).asRoute()
}
