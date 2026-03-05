package br.gov.serpro.vaas.dto

import br.gov.serpro.vaas.domain.AuthType
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "authType")
@JsonSubTypes(
        JsonSubTypes.Type(value = BasicCredentialDto::class, name = "BASIC"),
        JsonSubTypes.Type(value = OAuth2CredentialDto::class, name = "OAUTH2")
)
abstract class CredentialDto {
    abstract val authType: AuthType?
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BasicCredentialDto(val username: String? = null, val password: String? = null) :
        CredentialDto() {
    override val authType: AuthType = AuthType.BASIC
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OAuth2CredentialDto(
        val tokenUrl: String? = null,
        val clientId: String? = null,
        val clientSecret: String? = null
) : CredentialDto() {
    override val authType: AuthType = AuthType.OAUTH2
}
