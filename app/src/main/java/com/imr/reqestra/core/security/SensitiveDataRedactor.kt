package com.imr.reqestra.core.security

import com.imr.reqestra.domain.model.ParsedHeader
import java.net.URI
import java.net.URLDecoder

object SensitiveDataRedactor {
    private val sensitiveHeader = Regex(
        "authorization|proxy-authorization|cookie|set-cookie|api[-_]?key|token|secret|password|credential|session|jwt|csrf|xsrf",
        RegexOption.IGNORE_CASE,
    )
    private val sensitiveQueryName = Regex(
        "^(token|access_token|refresh_token|api_key|apikey|key|secret|password|passwd|session|sid|jwt|code|client_secret)$",
        RegexOption.IGNORE_CASE,
    )
    fun redact(headers: List<ParsedHeader>): List<ParsedHeader> = headers.map { header ->
        if (sensitiveHeader.containsMatchIn(header.name)) header.copy(value = "••••••••") else header
    }
    fun redactUrl(rawUrl: String): String = runCatching {
        val uri = URI(rawUrl)
        // No reconstruir con URI(..., query, ...): recodifica %2F como %252F.
        val beforeQuery = rawUrl.substringBefore('?').substringBefore('#')
        val query = uri.rawQuery ?: return@runCatching beforeQuery
        val safeQuery = query.split('&').joinToString("&") { pair ->
            val encodedName = pair.substringBefore('=')
            val decodedName = runCatching {
                URLDecoder.decode(encodedName.replace("+", "%2B"), "UTF-8")
            }.getOrDefault(encodedName)
            if (sensitiveQueryName.matches(decodedName)) "$encodedName=REDACTED" else pair
        }
        "$beforeQuery?$safeQuery"
    }.getOrElse { rawUrl.substringBefore('#').substringBefore('?') }
}
