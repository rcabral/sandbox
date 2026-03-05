package br.gov.serpro.vaas.security

import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders

@RequestScoped
class ClientContext {
    
    @Context
    lateinit var headers: HttpHeaders

    fun getClientId(): String? {
        // Tenta extrair do header "X-Client-Id" como sugerido, 
        // ou poderia ser mapeado do Token JWT real injetando autowired JsonWebToken
        return headers.getHeaderString("X-Client-Id")
    }
}
