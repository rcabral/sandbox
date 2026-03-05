package br.gov.serpro.datavalid.extendable.domain

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.net.URI

@Entity
@Table(name = "sources")
class Source : PanacheEntityBase {
    @Id
    lateinit var id: String

    @Column(nullable = false)
    lateinit var uri: String

    @Embedded
    var credential: Credential? = null

    @Column(name = "is_public", nullable = false)
    var isPublic: Boolean = true

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "source_allowed_clients", joinColumns = [JoinColumn(name = "source_id")])
    @Column(name = "client_id")
    var allowedClientIds: Set<String> = mutableSetOf()

    constructor()

    constructor(id: String, uri: String, credential: Credential?, isPublic: Boolean, allowedClientIds: Set<String>) {
        this.id = id
        this.uri = uri
        this.credential = credential
        this.isPublic = isPublic
        this.allowedClientIds = allowedClientIds
    }

    fun resourceLocation(): URI {
        return URI("#/v1/source/${this.id}")
    }
}
