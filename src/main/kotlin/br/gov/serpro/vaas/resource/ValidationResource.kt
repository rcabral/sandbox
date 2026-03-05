package br.gov.serpro.vaas.resource

import br.gov.serpro.vaas.dto.ValidationRequest
import br.gov.serpro.vaas.service.ValidationService
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

@Path("/v1/validation")
class ValidationResource {

    @Inject lateinit var service: ValidationService

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Realiza a validação de atributos",
            description = "Validação combinada de múltiplos itens (Registration, Facial, etc)"
    )
    @APIResponses(
            APIResponse(responseCode = "200", description = "OK"),
            APIResponse(responseCode = "400", description = "Bad Request"),
            APIResponse(responseCode = "401", description = "Unauthorized"),
            APIResponse(responseCode = "403", description = "Forbidden (ACL Denied)"),
            APIResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity - Regra de Negócio"
            )
    )
    @SecurityRequirement(name = "getToken")
    fun cria(
            @RestHeader("Authorization") authorization: String?,
            @RequestBody(
                    description = "ValidationRequest",
                    required = true,
                    content =
                            [
                                    Content(
                                            mediaType = MediaType.APPLICATION_JSON,
                                            example = EXEMPLO_DE_VALIDACAO
                                    )]
            )
            request: ValidationRequest
    ): Response {
        val validation = service.validate(request)
        return Response.ok(validation).build()
    }
}

private const val EXEMPLO_DE_VALIDACAO =
        """{
  "sourceId": "ec0df5c8-40a7-4bbe-bad8-de075c8e2813",
  "key_attribute": "dokumento_identifikashon",
  "key_value": "CW123456",
  "validations": [
   { "attribute": "nomber", "value": "Javier Pieters", "path": "nomber", "type": "REGISTRATION" },
   { "attribute": "foto", "value": "iVBORw0KGgoAAAANSUhEUgAAAAUA", "path": "foto", "type": "FACIAL" },
   { "attribute": "digital", "value": "Base64Fingerprint==", "path": "digital", "type": "DIGITAL" }
  ]
}"""
