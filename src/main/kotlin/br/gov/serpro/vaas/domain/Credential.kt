package br.gov.serpro.vaas.domain

import jakarta.persistence.*

@Entity
@Table(name = "credentials")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "auth_type", discriminatorType = DiscriminatorType.STRING)
abstract class Credential {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null

    abstract fun getAuthTypeEnum(): AuthType
}

@Entity
@DiscriminatorValue("BASIC")
class BasicCredential : Credential {
    var username: String? = null
    var password: String? = null

    constructor()

    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }

    override fun getAuthTypeEnum(): AuthType = AuthType.BASIC
}

@Entity
@DiscriminatorValue("OAUTH2")
class OAuth2Credential : Credential {
    var tokenUrl: String? = null
    var clientId: String? = null
    var clientSecret: String? = null

    constructor()

    constructor(tokenUrl: String?, clientId: String?, clientSecret: String?) {
        this.tokenUrl = tokenUrl
        this.clientId = clientId
        this.clientSecret = clientSecret
    }

    override fun getAuthTypeEnum(): AuthType = AuthType.OAUTH2
}
