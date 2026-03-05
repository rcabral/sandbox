package br.gov.serpro.datavalid.extendable.domain

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class Credential {
    @Enumerated(EnumType.STRING)
    var authType: AuthType? = null

    // For BASIC
    var username: String? = null
    var password: String? = null

    // For OAUTH2
    var tokenUrl: String? = null
    var clientId: String? = null
    var clientSecret: String? = null

    constructor()

    constructor(
        authType: AuthType? = AuthType.BASIC,
        username: String? = null,
        password: String? = null,
        tokenUrl: String? = null,
        clientId: String? = null,
        clientSecret: String? = null
    ) {
        this.authType = authType
        this.username = username
        this.password = password
        this.tokenUrl = tokenUrl
        this.clientId = clientId
        this.clientSecret = clientSecret
    }
}
