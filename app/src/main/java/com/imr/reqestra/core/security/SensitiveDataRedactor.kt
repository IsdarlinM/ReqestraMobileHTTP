package com.imr.reqestra.core.security

import com.imr.reqestra.domain.model.ParsedHeader
import java.net.URI

object SensitiveDataRedactor {
    private val sensitiveHeader = Regex(
        "^(authorization|proxy-authorization|cookie|set-cookie|x-api-key|api-key|x-auth-token|x-csrf-token|x-xsrf-token)$",
        RegexOption.IGNORE_CASE,
    )
    private val sensitiveQueryName = Regex(
        "^(token|access_token|refresh_token|api_key|apikey|key|secret|password|passwd|session|sid|jwt|code)$",
        RegexOption.IGNORE_CASE,
    )

    fun redact(headers: List<ParsedHeader>): List<ParsedHeader> = headers.map { header ->
        if (sensitiveHeader.matches(header.name)) header.copy(value = "••••••••") else header
    }

    fun redactUrl(rawUrl: String): String = runCatching {
        val uri = URI(rawUrl)
        val safeQuery = uri.rawQuery
            ?.split('&')
            ?.joinToString("&") { pair ->
                val name = pair.substringBefore('=')
                if (sensitiveQueryName.matches(name)) "$name=REDACTED" else pair
            }
        URI(uri.scheme, null, uri.host, uri.port, uri.path, safeQuery, null).toASCIIString()
    }.getOrDefault(rawUrl.substringBefore('#'))
}
