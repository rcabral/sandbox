package br.gov.serpro.vaas.service

import br.gov.serpro.vaas.client.SourceApiClient
import br.gov.serpro.vaas.domain.Source
import br.gov.serpro.vaas.dto.ValidationItem
import br.gov.serpro.vaas.dto.ValidationRequest
import br.gov.serpro.vaas.dto.ValidationType
import br.gov.serpro.vaas.security.ClientContext
import jakarta.ws.rs.ForbiddenException
import org.jboss.logging.Logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class ValidationServiceTest {

    private lateinit var validationService: ValidationService
    private lateinit var sourceServiceMock: SourceService
    private lateinit var clientContextMock: ClientContext
    private lateinit var sourceApiClientMock: SourceApiClient
    private lateinit var loggerMock: Logger

    @BeforeEach
    fun setUp() {
        sourceServiceMock = mock(SourceService::class.java)
        clientContextMock = mock(ClientContext::class.java)
        sourceApiClientMock = mock(SourceApiClient::class.java)
        loggerMock = mock(Logger::class.java)

        validationService = ValidationService()
        validationService.sourceService = sourceServiceMock
        validationService.clientContext = clientContextMock
        validationService.sourceApiClient = sourceApiClientMock
        validationService.logger = loggerMock
    }

    @Test
    fun `test validate retrieves source and processes correctly`() {
        val request =
                ValidationRequest(
                        "source-doc",
                        "cpf",
                        "123",
                        listOf(ValidationItem("nome", "Joao", "nome", ValidationType.REGISTRATION))
                )

        val source = mock(Source::class.java)
        `when`(source.isPublic).thenReturn(true)
        `when`(sourceServiceMock.recuperaEntidade("source-doc")).thenReturn(source)

        val jsonResponse = """[{"nome": "Joao"}]"""
        `when`(sourceApiClientMock.getApiResponse(source, request)).thenReturn(jsonResponse)

        val response = validationService.validate(request)

        assertNotNull(response)
        assertEquals("source-doc", response?.sourceId)
        val result = response?.validation_results?.first()
        assertEquals(true, result?.APPROVED)
    }

    @Test
    fun `test validate throws exception if private source and client invalid`() {
        val request = ValidationRequest("source-priv", "cpf", "111", listOf())

        val source = mock(Source::class.java)
        `when`(source.id).thenReturn("source-priv")
        `when`(source.isPublic).thenReturn(false)
        `when`(source.allowedClientIds).thenReturn(setOf("valid-client"))

        `when`(sourceServiceMock.recuperaEntidade("source-priv")).thenReturn(source)
        `when`(clientContextMock.getClientId()).thenReturn("invalid-client")

        assertThrows(ForbiddenException::class.java) { validationService.validate(request) }
    }
}
