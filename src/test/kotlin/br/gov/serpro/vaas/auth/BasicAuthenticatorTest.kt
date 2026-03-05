package br.gov.serpro.vaas.auth

import br.gov.serpro.vaas.domain.BasicCredential
import io.quarkus.test.junit.QuarkusTest
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@QuarkusTest
class BasicAuthenticatorTest {

    private val authenticator = BasicAuthenticator()

    @Test
    fun `test supports BasicCredential`() {
        val credential = BasicCredential("user", "pass")
        assertTrue(authenticator.supports(credential))
    }

    @Test
    fun `test authenticate sets correct Authorization header`() {
        val credential = BasicCredential("testuser", "testpass")

        val connection =
                object : HttpURLConnection(URL("http://dummy.url")) {
                    val headers = mutableMapOf<String, String>()
                    override fun setRequestProperty(key: String, value: String) {
                        headers[key] = value
                    }
                    override fun getRequestProperty(key: String): String? {
                        return headers[key]
                    }
                    override fun connect() {}
                    override fun disconnect() {}
                    override fun usingProxy() = false
                }

        authenticator.authenticate(connection, credential)

        val expectedAuth = Base64.getEncoder().encodeToString("testuser:testpass".toByteArray())
        assertEquals("Basic $expectedAuth", connection.getRequestProperty("Authorization"))
    }
}
