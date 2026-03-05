package br.gov.serpro.vaas.validation

import br.gov.serpro.vaas.dto.Classification
import br.gov.serpro.vaas.dto.ValidationResult

class FacialValidation : Validation {
    override fun execute(attribute: String, value: String, referenceValue: String): ValidationResult {
        // TODO: Implementar chamada ao componente externo (AIBIO)
        val similarity = 0.96
        val approved = similarity >= 0.85
        val classification = Classification.VERY_HIGH_PROBABILITY

        return ValidationResult(
            attribute = attribute,
            APPROVED = approved,
            similarity = similarity,
            classification = classification
        )
    }
}
