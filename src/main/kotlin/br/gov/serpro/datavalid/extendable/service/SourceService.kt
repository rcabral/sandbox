package br.gov.serpro.datavalid.extendable.service

import br.gov.serpro.datavalid.extendable.dao.SourceDao
import br.gov.serpro.datavalid.extendable.domain.Credential
import br.gov.serpro.datavalid.extendable.domain.Source
import br.gov.serpro.datavalid.extendable.dto.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import org.jboss.logging.Logger
import java.net.URI

@ApplicationScoped
class SourceService {

    @Inject
    lateinit var sourceDao: SourceDao
    
    @Inject
    lateinit var logger: Logger

    @Transactional
    fun persiste(id: String, sourceInfo: SourceInfo): SourceResponse {
        logger.info("Tentando persistir Source com id $id e URI ${sourceInfo.uri}")
        try {
            URI(sourceInfo.uri).toURL()
        } catch (e: Exception) {
            throw BadRequestException("Invalid URI")
        }

        val credential = sourceInfo.credential?.let {
            Credential(it.authType, it.username, it.password, it.tokenUrl, it.clientId, it.clientSecret)
        }

        val source = Source(id, sourceInfo.uri, credential, sourceInfo.isPublic, sourceInfo.allowedClientIds)
        sourceDao.persist(source)
        logger.info("Source com id $id persistido com sucesso")
        
        return toResponse(source)
    }

    fun recupera(id: String): SourceResponse? {
        logger.debug("Recuperando Source com id $id")
        val source = sourceDao.findById(id) ?: return null
        return toResponse(source)
    }

    fun recuperaEntidade(id: String): Source? {
        return sourceDao.findById(id)
    }

    fun listaTodosId(): List<String> {
        logger.debug("Listando todos os IDs de Sources")
        return sourceDao.findAll().stream().map { it.id }.toList()
    }

    @Transactional
    fun atualiza(id: String, sourceInfo: SourceInfo): SourceResponse {
        logger.info("Atualizando Source com id $id")
        val source = sourceDao.findById(id) ?: throw NotFoundException("Source not found")
        source.uri = sourceInfo.uri
        source.isPublic = sourceInfo.isPublic
        source.allowedClientIds = sourceInfo.allowedClientIds
        
        source.credential = sourceInfo.credential?.let {
            Credential(it.authType, it.username, it.password, it.tokenUrl, it.clientId, it.clientSecret)
        }
        
        sourceDao.persist(source)
        return toResponse(source)
    }

    @Transactional
    fun remove(id: String) {
        logger.info("Removendo Source com id $id")
        sourceDao.deleteById(id)
    }

    private fun toResponse(source: Source): SourceResponse {
        val credentialDto = source.credential?.let {
            CredentialDto(it.authType, it.username, it.password, it.tokenUrl, it.clientId, it.clientSecret)
        }

        val acoes = createListaAcoes(source.id)
        
        return SourceResponse(source.id, source.uri, credentialDto, source.isPublic, source.allowedClientIds, acoes)
    }

    private fun createListaAcoes(clientId: String): Collection<Acao> {
        val uri = URI("#/v1/source/$clientId").toString()
        return listOf(
            Acao("atualiza", uri, Method.PUT),
            Acao("remove", uri, Method.DELETE),
            Acao("recupera", uri, Method.GET)
        )
    }
}
