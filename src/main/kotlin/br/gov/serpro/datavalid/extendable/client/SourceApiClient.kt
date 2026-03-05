package br.gov.serpro.datavalid.extendable.client

import br.gov.serpro.datavalid.extendable.auth.Authenticator
import br.gov.serpro.datavalid.extendable.domain.Source
import br.gov.serpro.datavalid.extendable.dto.ValidationRequest
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.jboss.logging.Logger

@ApplicationScoped
class SourceApiClient {

    @Inject lateinit var logger: Logger

    @Inject lateinit var authenticators: Instance<Authenticator>

    fun getApiResponse(source: Source, request: ValidationRequest): String? {
        return try {
            val encodedValue =
                    URLEncoder.encode(request.key_value, StandardCharsets.UTF_8.toString())
            val apiUrl = "${source.uri}?${request.key_attribute}=$encodedValue"

            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            source.credential?.let { cred ->
                authenticators.find { it.supports(cred) }?.authenticate(connection, cred)
                        ?: logger.warn(
                                "Nenhuma estratégia de autenticação encontrada para a credencial."
                        )
            }

            connection.connect()

            if (connection.responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Erro ao chamar API externa!", e)
            null
        }
    }
}
