package br.gov.serpro.datavalid.extendable.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.net.URI

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SourceResponse(
    val id: String,
    val uri: String,
    val credential: CredentialDto?,
    val isPublic: Boolean,
    val allowedClientIds: Set<String>,
    val acoes: Collection<Acao>
) {
    fun resourceLocation(): URI {
        return URI("#/v1/source/$id")
    }
}
