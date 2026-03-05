package br.gov.serpro.vaas.dao

import br.gov.serpro.vaas.domain.Source
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SourceDao : PanacheRepositoryBase<Source, String>
