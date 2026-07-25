package com.imr.reqestra.core.request

import com.imr.reqestra.core.validation.RequestValidator
import com.imr.reqestra.domain.model.ParsedHeader

data class ComposedRequestMetadata(
    val headers: List<ParsedHeader>,
    val cookies: Map<String, String>,
)

object RequestComposer {
    fun compose(
        sessionHeaders: List<ParsedHeader>,
        requestHeaders: List<ParsedHeader>,
        sessionCookies: Map<String, String>,
        manualCookies: Map<String, String>,
    ): ComposedRequestMetadata {
        var headers = RequestValidator.mergeHeaders(sessionHeaders, requestHeaders)
        val headerCookies = RequestValidator.parseCookieHeader(headers)
        headers = headers.filterNot { it.name.equals("Cookie", ignoreCase = true) }

        val cookies = linkedMapOf<String, String>().apply {
            putAll(sessionCookies)
            putAll(headerCookies)
            putAll(manualCookies)
        }
        if (cookies.isNotEmpty()) {
            headers = headers + ParsedHeader(
                name = "Cookie",
                value = cookies.entries.joinToString("; ") { "${it.key}=${it.value}" },
            )
        }
        return ComposedRequestMetadata(headers, cookies)
    }
}
