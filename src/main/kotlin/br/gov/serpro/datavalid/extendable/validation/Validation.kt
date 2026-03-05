package br.gov.serpro.datavalid.extendable.validation

import br.gov.serpro.datavalid.extendable.dto.ValidationResult
import br.gov.serpro.datavalid.extendable.dto.ValidationType

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
