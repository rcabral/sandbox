package br.gov.serpro.datavalid.extendable.auth

import br.gov.serpro.datavalid.extendable.domain.OAuth2Credential
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpServer
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL
import org.jboss.logging.Logger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OAuth2AuthenticatorTest {

    private lateinit var authenticator: OAuth2Authenticator
    private lateinit var mockServer: HttpServer
    private var requestCount = 0

    @BeforeEach
    fun setup() {
        val logger = Logger.getLogger(OAuth2Authenticator::class.java)
        val objectMapper = ObjectMapper()
        authenticator = OAuth2Authenticator(logger, objectMapper)
        requestCount = 0

        // Inicia o servidor mock
        mockServer = HttpServer.create(InetSocketAddress(0), 0)
        mockServer.createContext("/oauth/token") { exchange ->
            if (exchange.requestMethod == "POST") {
                requestCount++
                val response = """{"access_token": "mocked_real_token", "expires_in": 3600}"""
                exchange.sendResponseHeaders(200, response.length.toLong())
                exchange.responseBody.use { os -> os.write(response.toByteArray()) }
            } else {
                exchange.sendResponseHeaders(405, -1)
            }
        }
        mockServer.start()
    }

    @AfterEach
    fun teardown() {
        mockServer.stop(0)
    }

    @Test
    fun `test authenticate fetches token and uses cache`() {
        val port = mockServer.address.port
        val tokenUrl = "http://localhost:${port}/oauth/token"

        val credential = OAuth2Credential(tokenUrl, "client123", "secret123")

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

        // Primeira chamada deve buscar do servidor (requestCount = 1)
        authenticator.authenticate(connection, credential)

        assertEquals(1, requestCount, "Deve ter feito 1 chamada HTTP")
        assertEquals("Bearer mocked_real_token", connection.getRequestProperty("Authorization"))

        // Segunda chamada deve vir do cache (requestCount = 1)
        val connection2 =
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
        authenticator.authenticate(connection2, credential)

        assertEquals(1, requestCount, "Deve usar o cache, não fazer nova chamada")
        assertEquals("Bearer mocked_real_token", connection2.getRequestProperty("Authorization"))
    }
}
