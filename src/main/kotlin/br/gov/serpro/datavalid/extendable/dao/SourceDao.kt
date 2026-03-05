package br.gov.serpro.datavalid.extendable.dao

import br.gov.serpro.datavalid.extendable.domain.Source
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SourceDao : PanacheRepositoryBase<Source, String>
