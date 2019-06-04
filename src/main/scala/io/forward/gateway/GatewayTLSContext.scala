package io.forward.gateway

import java.io.InputStream
import java.security.{ SecureRandom, KeyStore }
import javax.net.ssl.{ SSLContext, TrustManagerFactory, KeyManagerFactory }
import akka.http.scaladsl.{ ConnectionContext, HttpsConnectionContext}

final class GatewayTLSContext(password: String) {
  private val ks = getKeyStore("server.p12", password)
  private val sslContext = getSSLContext(password)
  private val sunX509 = "SunX509"

  val https: HttpsConnectionContext = ConnectionContext.https(sslContext)

  private def getKeyStore(p12Name: String, password: String): KeyStore = {
    val keystore: InputStream = getClass.getClassLoader.getResourceAsStream(p12Name)
    KeyStore.getInstance("PKCS12").load(keystore, password.toCharArray)
    ks
  }

  private def getSSLContext(password: String): SSLContext = {
    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance(sunX509)
    keyManagerFactory.init(ks, password.toCharArray)

    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(sunX509)
    tmf.init(ks)

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)

    sslContext
  }
}
