package br.gov.serpro.vaas.auth

import br.gov.serpro.vaas.domain.Credential
import br.gov.serpro.vaas.domain.OAuth2Credential
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import org.jboss.logging.Logger

data class TokenInfo(val accessToken: String, val expiresAt: Instant)

@ApplicationScoped
class OAuth2Authenticator(private val logger: Logger, private val objectMapper: ObjectMapper) :
        Authenticator {

    private val httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()

    private val tokenCache = ConcurrentHashMap<String, TokenInfo>()

    // Margem de segurança de renovação em segundos (ex: 5 minutos)
    private val EXPIRATION_MARGIN_SECONDS = 300L

    override fun supports(credential: Credential): Boolean {
        return credential is OAuth2Credential
    }

    override fun authenticate(connection: HttpURLConnection, credential: Credential) {
        val oauth2 = credential as OAuth2Credential
        val cacheKey = "${oauth2.tokenUrl}::${oauth2.clientId}"

        var tokenInfo = tokenCache[cacheKey]

        val now = Instant.now()
        // Se Token expirou (ou está muito próximo, dentro da margem de 300s) ou não existe
        if (tokenInfo == null ||
                        now.isAfter(tokenInfo.expiresAt.minusSeconds(EXPIRATION_MARGIN_SECONDS))
        ) {
            logger.info(
                    "Token OAuth2 expirado (ou proximo de expirar) ou inexistente. Solicitando novo token para a url ${oauth2.tokenUrl}"
            )
            tokenInfo = fetchNewToken(oauth2)
            tokenCache[cacheKey] = tokenInfo
        } else {
            logger.debug("Usando token OAuth2 em cache válido para client ${oauth2.clientId}")
        }

        connection.setRequestProperty("Authorization", "Bearer ${tokenInfo.accessToken}")
    }

    private fun fetchNewToken(oauth2: OAuth2Credential): TokenInfo {
        val requestBody = buildRequestBody(oauth2)

        val request =
                HttpRequest.newBuilder()
                        .uri(URI.create(oauth2.tokenUrl))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() !in 200..299) {
            val errorMsg =
                    "Erro ao buscar token OAuth2. Status: ${response.statusCode()}, Body: ${response.body()}"
            logger.error(errorMsg)
            throw RuntimeException(errorMsg)
        }

        val jsonNode = objectMapper.readTree(response.body())
        val accessToken =
                jsonNode.get("access_token")?.asText()
                        ?: throw RuntimeException("Campo access_token não encontrado na resposta.")

        val expiresInString = jsonNode.get("expires_in")?.asText() ?: "0"
        val expiresInSeconds = expiresInString.toLongOrNull() ?: 0L

        val expiresAt = Instant.now().plusSeconds(expiresInSeconds)

        return TokenInfo(accessToken, expiresAt)
    }

    private fun buildRequestBody(oauth2: OAuth2Credential): String {
        return "grant_type=client_credentials" +
                "&client_id=${URLEncoder.encode(oauth2.clientId ?: "", StandardCharsets.UTF_8)}" +
                "&client_secret=${URLEncoder.encode(oauth2.clientSecret ?: "", StandardCharsets.UTF_8)}"
    }
}
