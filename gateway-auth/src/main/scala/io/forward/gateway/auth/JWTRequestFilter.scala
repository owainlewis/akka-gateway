package io.forward.gateway.auth

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive0}
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import akka.http.scaladsl.server.Directives._

import scala.util.{Failure, Success, Try}

final class JWTRequestFilter(secret: String) {

  private lazy val verifier = JWT.require(Algorithm.HMAC256(secret)).withIssuer("auth0").build

  def authenticateJWT: Directive0 = {
    optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken).flatMap {
      case Some(token) =>
        Try { verifier.verify(token) } match {
          case Success(_) => pass
          case Failure(_) => reject(AuthorizationFailedRejection)
        }
      case None => reject(AuthorizationFailedRejection)
    }
  }

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }
}
