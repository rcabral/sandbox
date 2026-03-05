package br.gov.serpro.vaas.validation

import br.gov.serpro.vaas.dto.Classification
import br.gov.serpro.vaas.dto.ValidationResult

class DigitalValidation : Validation {
    override fun execute(attribute: String, value: String, referenceValue: String): ValidationResult {
        // TODO: Implementar chamada ao componente externo (AIBIO)
        val similarity = 0.92
        val approved = true

        return ValidationResult(
            attribute = attribute,
            APPROVED = approved,
            similarity = similarity,
            classification = Classification.HIGH_PROBABILITY
        )
    }
}
