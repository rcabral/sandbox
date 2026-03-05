package br.gov.serpro.vaas.dto

data class SourceInfo(
    val uri: String,
    val credential: CredentialDto?,
    val isPublic: Boolean = true,
    val allowedClientIds: Set<String> = emptySet()
)
