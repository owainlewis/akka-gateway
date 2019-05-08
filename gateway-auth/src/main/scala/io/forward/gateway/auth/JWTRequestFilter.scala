package io.forward.gateway.auth

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive0}
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

import scala.util.{Failure, Success, Try}

final class JWTRequestFilter(secret: String) {

  private lazy val verifier = JWT.require(Algorithm.HMAC256(secret)).withIssuer("auth0").build

  def check(token: String): Directive0 = {
    Try { verifier.verify(token) } match {
      case Success(_) => pass
      case Failure(_) => reject(AuthorizationFailedRejection)
    }
  }
}
