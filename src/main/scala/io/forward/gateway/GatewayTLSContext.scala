package io.forward.gateway

import java.io.InputStream
import java.security.{ SecureRandom, KeyStore }
import javax.net.ssl.{ SSLContext, TrustManagerFactory, KeyManagerFactory }
import akka.http.scaladsl.{ ConnectionContext, HttpsConnectionContext}

final class GatewayTLSContext(password: String) {
  private val ks = getKeyStore("serer.p12", password)
  private val sslContext = getSSLContext(password)

  val https: HttpsConnectionContext = ConnectionContext.https(sslContext)

  private def getKeyStore(p12Name: String, password: String): KeyStore = {
    val keystore: InputStream = getClass.getClassLoader.getResourceAsStream(p12Name)
    KeyStore.getInstance("PKCS12").load(keystore, password.toCharArray)
    ks
  }

  private def getSSLContext(password: String): SSLContext = {
    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(ks, password.toCharArray)

    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    tmf.init(ks)

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)

    sslContext
  }
}