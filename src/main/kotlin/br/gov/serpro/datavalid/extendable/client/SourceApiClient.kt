package br.gov.serpro.datavalid.extendable.client

import br.gov.serpro.datavalid.extendable.auth.Authenticator
import br.gov.serpro.datavalid.extendable.domain.Credential
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import java.net.HttpURLConnection
import java.net.URL
import org.jboss.logging.Logger

@ApplicationScoped
class SourceApiClient {

    @Inject lateinit var logger: Logger

    @Inject lateinit var authenticators: Instance<Authenticator>

    fun getApiResponse(apiUrl: String, credential: Credential?): String? {
        return try {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            credential?.let { cred ->
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
