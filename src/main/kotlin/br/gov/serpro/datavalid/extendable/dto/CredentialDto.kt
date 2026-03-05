package br.gov.serpro.datavalid.extendable.dto

import br.gov.serpro.datavalid.extendable.domain.AuthType
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CredentialDto(
    val authType: AuthType? = AuthType.BASIC,
    val username: String? = null,
    val password: String? = null,
    val tokenUrl: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null
)
