package com.imr.reqestra.core.variables

import com.imr.reqestra.domain.model.RequestDraft
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/** Parámetros de query estructurados: nombre=valor, una pareja por línea. */
object RequestParameterizer {
    private val queryName = Regex("[A-Za-z_][A-Za-z0-9_.-]{0,79}")
    const val MAX_QUERY_PARAMS = 40

    fun parseQuery(text: String): List<Pair<String, String>> {
        require(text.length <= 12_000) { "La lista de parámetros es demasiado grande." }
        val lines = text.lineSequence().map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }.toList()
        require(lines.size <= MAX_QUERY_PARAMS) { "Máximo $MAX_QUERY_PARAMS parámetros." }
        return lines.mapIndexed { index, line ->
            val eq = line.indexOf('=')
            require(eq > 0) { "Parámetro ${index + 1}: usa nombre=valor." }
            val name = line.substring(0, eq).trim()
            val value = line.substring(eq + 1)
            require(queryName.matches(name)) { "Nombre de parámetro no válido: $name." }
            require(value.length <= 2048 && value.none { it.code < 0x20 }) {
                "El valor del parámetro $name no es válido."
            }
            name to value
        }
    }

    fun resolve(draft: RequestDraft, variables: Map<String, String>): RequestDraft {
        val url = VariableTemplate.resolve(draft.url, variables)
        val rows = parseQuery(VariableTemplate.resolve(draft.queryParamsText, variables))
        val expandedUrl = if (rows.isEmpty()) {
            url
        } else {
            val parsed = url.toHttpUrlOrNull() ?: error("No se puede agregar query: URL no válida.")
            parsed.newBuilder().apply {
                rows.forEach { (name, value) -> addQueryParameter(name, value) }
            }.build().toString()
        }
        return draft.copy(
            url = expandedUrl,
            queryParamsText = "",
            headersText = VariableTemplate.resolve(draft.headersText, variables),
            cookiesText = VariableTemplate.resolve(draft.cookiesText, variables),
            body = VariableTemplate.resolve(draft.body, variables),
            contentType = VariableTemplate.resolve(draft.contentType, variables),
        )
    }
}
