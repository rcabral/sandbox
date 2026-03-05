package br.gov.serpro.datavalid.extendable.dto

data class Acao(
    var rel: String,
    val uri: String,
    val method: Method
)
