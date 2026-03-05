package br.gov.serpro.vaas.service

import br.gov.serpro.vaas.client.SourceApiClient
import br.gov.serpro.vaas.dto.ValidationItem
import br.gov.serpro.vaas.dto.ValidationRequest
import br.gov.serpro.vaas.dto.ValidationResponse
import br.gov.serpro.vaas.dto.ValidationResult
import br.gov.serpro.vaas.security.ClientContext
import br.gov.serpro.vaas.validation.Validation
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import org.jboss.logging.Logger

@ApplicationScoped
class ValidationService {

    @Inject lateinit var sourceService: SourceService

    @Inject lateinit var clientContext: ClientContext

    @Inject lateinit var logger: Logger

    @Inject lateinit var sourceApiClient: SourceApiClient

    private val objectMapper = jacksonObjectMapper()

    fun validate(request: ValidationRequest): ValidationResponse? {
        logger.info("Iniciando validação para o sourceId: ${request.sourceId}")
        val source = sourceService.recuperaEntidade(request.sourceId) ?: return null

        // Checagem de ACL (RF-06)
        if (!source.isPublic) {
            val clientId = clientContext.getClientId()
            if (clientId == null || !source.allowedClientIds.contains(clientId)) {
                logger.warn(
                        "Acesso negado ao clientId $clientId para a source privada ${source.id}"
                )
                throw ForbiddenException("O cliente não possui acesso a esta fonte primária.")
            }
        }

        val responseJson = sourceApiClient.getApiResponse(source, request) ?: return null

        try {
            val responseDataList: List<Map<String, Any>> =
                    objectMapper.readValue(responseJson, List::class.java) as List<Map<String, Any>>
            val responseData = responseDataList.firstOrNull() ?: return null

            val validationResults = execute(request.validations, responseData)

            logger.info("Validação concluída com sucesso para o sourceId: ${request.sourceId}")
            return ValidationResponse(
                    request.sourceId,
                    request.key_attribute,
                    request.key_value,
                    validationResults
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun execute(
            validations: List<ValidationItem>,
            responseData: Map<String, Any>
    ): List<ValidationResult> {
        return validations.map { validationItem ->
            val expectedValue = responseData[validationItem.path]?.toString() ?: ""
            val validation = Validation.create(validationItem.type)
            validation.execute(validationItem.attribute, validationItem.value, expectedValue)
        }
    }
}
