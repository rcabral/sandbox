package br.gov.serpro.vaas.validation

import br.gov.serpro.vaas.dto.ValidationResult
import br.gov.serpro.vaas.dto.ValidationType

interface Validation {
    fun execute(attribute: String, value: String, referenceValue: String): ValidationResult

    companion object {
        fun create(type: ValidationType): Validation {
            return when (type) {
                ValidationType.REGISTRATION -> RegistrationValidation()
                ValidationType.FACIAL -> FacialValidation()
                ValidationType.FACIAL_WITH_LIVENESS -> FacialWithLivenessValidation()
                ValidationType.DIGITAL -> DigitalValidation()
                ValidationType.QR_CODE -> QRCodeValidation()
            }
        }
    }
}
