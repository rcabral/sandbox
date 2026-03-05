package br.gov.serpro.vaas.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ValidationResponse(
    val sourceId: String,
    val key_attribute: String,
    val key_value: String,
    val validation_results: List<ValidationResult>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ValidationResult(
    val attribute: String,
    val APPROVED: Boolean,
    val similarity: Double,
    val classification: Classification?
)

enum class Classification {
    VERY_HIGH_PROBABILITY,
    HIGH_PROBABILITY,
    LOW_PROBABILITY,
    VERY_LOW_PROBABILITY
}
