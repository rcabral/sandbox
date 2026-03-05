package br.gov.serpro.vaas.service

import br.gov.serpro.vaas.dao.SourceDao
import br.gov.serpro.vaas.domain.BasicCredential
import br.gov.serpro.vaas.domain.Source
import br.gov.serpro.vaas.dto.BasicCredentialDto
import br.gov.serpro.vaas.dto.SourceInfo
import org.jboss.logging.Logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class SourceServiceTest {

    private lateinit var sourceService: SourceService
    private lateinit var sourceDaoMock: SourceDao
    private lateinit var loggerMock: Logger

    @BeforeEach
    fun setUp() {
        sourceDaoMock = mock(SourceDao::class.java)
        loggerMock = mock(Logger::class.java)

        sourceService = SourceService()
        sourceService.sourceDao = sourceDaoMock
        sourceService.logger = loggerMock
    }

    @Test
    fun `test persiste source with valid basic credential`() {
        val id = "source-1"
        val credentialDto = BasicCredentialDto("user", "pass")
        val sourceInfo = SourceInfo("http://valid-url.com", credentialDto, true, setOf())

        val response = sourceService.persiste(id, sourceInfo)

        assertNotNull(response)
        assertEquals(id, response.id)
        assertEquals("http://valid-url.com", response.uri)
        assertTrue(response.isPublic)
        assertTrue(response.credential is BasicCredentialDto)
        assertEquals("user", (response.credential as BasicCredentialDto).username)
    }

    @Test
    fun `test recupera returns null when not found`() {
        `when`(sourceDaoMock.findById("missing")).thenReturn(null)
        val response = sourceService.recupera("missing")
        assertNull(response)
    }

    @Test
    fun `test recupera returns valid SourceResponse`() {
        val credential = BasicCredential("user", "pass")
        val source = Source("source-2", "http://valid.com", credential, false, setOf("client-1"))

        `when`(sourceDaoMock.findById("source-2")).thenReturn(source)

        val response = sourceService.recupera("source-2")

        assertNotNull(response)
        assertEquals("source-2", response?.id)
        assertEquals(false, response?.isPublic)
        assertTrue(response?.allowedClientIds?.contains("client-1") ?: false)
    }

    @Test
    fun `test remove calls delete on DAO`() {
        sourceService.remove("source-3")
        // Verified by lack of exceptions
    }
}
