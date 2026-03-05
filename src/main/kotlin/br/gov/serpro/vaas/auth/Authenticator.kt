package br.gov.serpro.vaas.auth

import br.gov.serpro.vaas.domain.Credential
import java.net.HttpURLConnection

interface Authenticator {
    fun authenticate(connection: HttpURLConnection, credential: Credential)
    fun supports(credential: Credential): Boolean
}
