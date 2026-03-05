package br.gov.serpro.datavalid.extendable.dto

import jakarta.validation.constraints.NotBlank

data class ValidationRequest(
    @field:NotBlank val sourceId: String,
    @field:NotBlank val key_attribute: String,
    @field:NotBlank val key_value: String,
    val validations: List<ValidationItem>
)

data class ValidationItem(
    @field:NotBlank val attribute: String,
    @field:NotBlank val value: String,
    @field:NotBlank val path: String,
    val type: ValidationType
)

enum class ValidationType {
    REGISTRATION,
    FACIAL,
    FACIAL_WITH_LIVENESS,
    DIGITAL,
    QR_CODE
}
