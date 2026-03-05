package br.gov.serpro.datavalid.extendable.validation

import br.gov.serpro.datavalid.extendable.dto.ValidationResult

class RegistrationValidation : Validation {
    override fun execute(attribute: String, value: String, referenceValue: String): ValidationResult {
        val similarity = calculateSimilarity(value, referenceValue)
        val approved = similarity >= 0.95

        return ValidationResult(
            attribute = attribute,
            APPROVED = approved,
            similarity = similarity,
            classification = null
        )
    }

    private fun calculateSimilarity(str1: String, str2: String): Double {
        val maxLength = maxOf(str1.length, str2.length)
        if (maxLength == 0) return 1.0 // Se ambos vazios, a similaridade é de 100%

        val commonChars = str1.commonPrefixWith(str2).length
        return commonChars.toDouble() / maxLength
    }
}
