package br.gov.serpro.datavalid.extendable.auth

import br.gov.serpro.datavalid.extendable.domain.Credential
import java.net.HttpURLConnection

interface Authenticator {
    fun authenticate(connection: HttpURLConnection, credential: Credential)
    fun supports(credential: Credential): Boolean
}
