package br.gov.serpro.datavalid.extendable.validation

import br.gov.serpro.datavalid.extendable.dto.Classification
import br.gov.serpro.datavalid.extendable.dto.ValidationResult

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
