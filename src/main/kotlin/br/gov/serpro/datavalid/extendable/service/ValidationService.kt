package br.gov.serpro.datavalid.extendable.service

import br.gov.serpro.datavalid.extendable.auth.Authenticator
import br.gov.serpro.datavalid.extendable.domain.Credential
import br.gov.serpro.datavalid.extendable.dto.ValidationItem
import br.gov.serpro.datavalid.extendable.dto.ValidationRequest
import br.gov.serpro.datavalid.extendable.dto.ValidationResponse
import br.gov.serpro.datavalid.extendable.dto.ValidationResult
import br.gov.serpro.datavalid.extendable.security.ClientContext
import br.gov.serpro.datavalid.extendable.validation.Validation
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import java.net.HttpURLConnection
import java.net.URL
import org.jboss.logging.Logger

@ApplicationScoped
class ValidationService {

    @Inject lateinit var sourceService: SourceService

    @Inject lateinit var clientContext: ClientContext

    @Inject lateinit var logger: Logger

    @Inject lateinit var authenticators: Instance<Authenticator>

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

        val apiUrl = "${source.uri}?${request.key_attribute}=${request.key_value}"
        val responseJson = getApiResponse(apiUrl, source.credential) ?: return null

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

    private fun getApiResponse(apiUrl: String, credential: Credential?): String? {
        return try {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            credential?.let { cred ->
                authenticators.find { it.supports(cred) }?.authenticate(connection, cred)
                        ?: logger.warn(
                                "Nenhuma estratégia de autenticação encontrada para a credencial."
                        )
            }

            connection.connect()

            if (connection.responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Erro ao chamar API externa!", e)
            null
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
