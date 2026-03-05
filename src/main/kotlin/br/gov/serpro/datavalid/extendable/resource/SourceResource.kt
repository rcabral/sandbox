package br.gov.serpro.datavalid.extendable.resource

import br.gov.serpro.datavalid.extendable.dto.SourceInfo
import br.gov.serpro.datavalid.extendable.service.SourceService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.jboss.resteasy.reactive.RestHeader
import java.util.*

@Path("/v1/source")
class SourceResource {

    @Inject
    lateinit var sourceService: SourceService

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cria uma nova Fonte de Dados", description = "Criação de Source apontando para uma URL externa com opções de Mock/ACL")
    @APIResponses(
        APIResponse(responseCode = "201", description = "Created"),
        APIResponse(responseCode = "400", description = "Bad Request"),
        APIResponse(responseCode = "401", description = "Unauthorized")
    )
    @SecurityRequirement(name = "getToken")
    fun cria(
        @RestHeader("Authorization") authorization: String?,
        @RequestBody(
            description = "Informações sobre a Fonte de Dados",
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                example = EXEMPLO_DE_SOURCE
            )]
        )
        sourceInfo: SourceInfo
    ): Response {
        val id = UUID.randomUUID().toString()
        val source = sourceService.persiste(id, sourceInfo)
        return Response.created(source.resourceLocation()).entity(source).build()
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Atualiza uma Fonte de Dados existente", description = "Atualização de parâmetros de autenticação e ACL de um Source")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "400", description = "Bad Request"),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "404", description = "Not Found")
    )
    @SecurityRequirement(name = "getToken")
    fun atualiza(
        @PathParam("id") id: String,
        @RestHeader("Authorization") authorization: String?,
        @RequestBody(
            description = "Informações atualizadas da Fonte de Dados",
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                example = EXEMPLO_DE_SOURCE
            )]
        )
        sourceInfo: SourceInfo
    ): Response {
        return try {
            val sourceAtualizado = sourceService.atualiza(id, sourceInfo)
            Response.ok().entity(sourceAtualizado).location(sourceAtualizado.resourceLocation()).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Recupera uma Fonte de Dados", description = "Recupera detalhes de um Source com base no seu ID")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "404", description = "Not Found")
    )
    @SecurityRequirement(name = "getToken")
    fun recupera(
        @PathParam("id") id: String,
        @RestHeader("Authorization") authorization: String?
    ): Response {
        val source = sourceService.recupera(id)
        return if (source != null) {
            Response.ok(source).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remove uma Fonte de Dados", description = "Remove logicamente ou fisicamente um Source com base no ID")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "400", description = "Bad Request"),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "404", description = "Not Found")
    )
    @SecurityRequirement(name = "getToken")
    fun remove(
        @PathParam("id") id: String,
        @RestHeader("Authorization") authorization: String?
    ): Response {
        val source = sourceService.recupera(id)
        return if (source != null) {
            sourceService.remove(id)
            Response.ok().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todos os IDs de Fontes de Dados", description = "Retorna um array com os IDs das fontes cadastradas (RF-03)")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "401", description = "Unauthorized")
    )
    @SecurityRequirement(name = "getToken")
    fun listaIds(@RestHeader("Authorization") authorization: String?): Response {
        val ids = sourceService.listaTodosId()
        return Response.ok(ids).build()
    }
}

private const val EXEMPLO_DE_SOURCE = """{
  "uri": "https://source-example",
  "isPublic": false,
  "allowedClientIds": ["mobile-app-1", "web-portal"],
  "credential": {
    "authType": "BASIC",
    "username": "username-example",
    "password": "password-example"
  }
}"""
