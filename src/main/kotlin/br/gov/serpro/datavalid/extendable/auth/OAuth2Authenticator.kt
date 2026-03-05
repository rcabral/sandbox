package br.gov.serpro.datavalid.extendable.auth

import br.gov.serpro.datavalid.extendable.domain.Credential
import br.gov.serpro.datavalid.extendable.domain.OAuth2Credential
import jakarta.enterprise.context.ApplicationScoped
import java.net.HttpURLConnection
import org.jboss.logging.Logger

@ApplicationScoped
class OAuth2Authenticator(private val logger: Logger) : Authenticator {

    override fun supports(credential: Credential): Boolean {
        return credential is OAuth2Credential
    }

    override fun authenticate(connection: HttpURLConnection, credential: Credential) {
        val oauth2 = credential as OAuth2Credential
        logger.info("Simulando geração de token OAuth2 para url ${oauth2.tokenUrl}")
        // Na prática faríamos outra request Http e pegaríamos o access_token
        val mockedToken = "mocked_oauth2_token_for_${oauth2.clientId}"
        connection.setRequestProperty("Authorization", "Bearer $mockedToken")
    }
}
