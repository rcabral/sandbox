package br.gov.serpro.datavalid.extendable.auth

import br.gov.serpro.datavalid.extendable.domain.BasicCredential
import br.gov.serpro.datavalid.extendable.domain.Credential
import jakarta.enterprise.context.ApplicationScoped
import java.net.HttpURLConnection
import java.util.Base64

@ApplicationScoped
class BasicAuthenticator : Authenticator {

    override fun supports(credential: Credential): Boolean {
        return credential is BasicCredential
    }

    override fun authenticate(connection: HttpURLConnection, credential: Credential) {
        val basic = credential as BasicCredential
        val auth = "${basic.username}:${basic.password}"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
        connection.setRequestProperty("Authorization", "Basic $encodedAuth")
    }
}
