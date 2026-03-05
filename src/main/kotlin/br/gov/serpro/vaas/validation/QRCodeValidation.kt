package br.gov.serpro.vaas.validation

import br.gov.serpro.vaas.dto.ValidationResult

class QRCodeValidation : Validation {
    override fun execute(attribute: String, value: String, referenceValue: String): ValidationResult {
        // TODO: Implementar chamada ao componente externo (VIODECODER)
        val similarity = 1.0
        val approved = true

        return ValidationResult(
            attribute = attribute,
            APPROVED = approved,
            similarity = similarity,
            classification = null
        )
    }
}
