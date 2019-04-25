package io.forward.gateway.filters

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Directive0, Directive1, Route}
import akka.stream.Materializer
import io.forward.gateway.core.backend.Backend
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object FilterDirectives {
  /**
    * Using the withPreFilter directive you can compose prefilters using the & operator
    *
    * val f1 = withPreFilter(a)
    * val f2 = withPreFilter(b)
    *
    * val f3 = f1 & f2
    *
    * @param filter A [[PreFilter]] to apply to a request
    * @return
    */
  def withPreFilter(filter: PreFilter): Directive0 =
    applyRequestFilter(filter).flatMap {
      case Left(response) => complete(response)
      case Right(request) => mapRequest(_ => request)
    }

  private def applyRequestFilter(filter: PreFilter): Directive1[Either[HttpResponse, HttpRequest]] = {
    Directive { inner => ctx =>
      implicit val ex: ExecutionContext = ctx.executionContext
      filter.onRequest(ctx.request).flatMap { result =>
        inner(Tuple1(result))(ctx)
      }
    }
  }
}

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
      onSuccess(pre.onRequest(request)) {
        case Left(response) => complete(response)
        case Right(r) => dispatch(backend, post, r)
      }
    }

  private def dispatch(upstream: Backend, post: PostFilter, request: HttpRequest): Route =
    onSuccess(upstream.apply(request)) { response =>
      val postFilteredResponse = unmarshallResponseEntity(response) flatMap (post.onResponse(response, _))
      onSuccess(postFilteredResponse) (complete(_))
    }

  private def unmarshallResponseEntity[T](response: HttpResponse): Future[String] =
    response.entity.withoutSizeLimit().toStrict(5.seconds).flatMap(Unmarshal(_).to[String])
}

object FilterChain {
  def apply(pre: PreFilter, backend: Backend, post: PostFilter)(implicit ex: ExecutionContext, mat: Materializer): Route =
    new FilterChain(pre, backend, post).asRoute()
}
