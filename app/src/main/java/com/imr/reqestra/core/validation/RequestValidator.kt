package com.imr.reqestra.core.validation

import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.BodyPolicy
import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.RequestDraft
import java.net.URI
import java.net.URISyntaxException
import java.util.Base64

object RequestValidator {
    const val MAX_BODY_BYTES = 5 * 1024 * 1024
    private const val MAX_URL_LENGTH = 8_192
    private const val MAX_HEADER_COUNT = 100
    private const val MAX_HEADER_VALUE_LENGTH = 16_384

    private val tokenRegex = Regex("^[!#$%&'*+.^_`|~0-9A-Za-z-]+$")
    private val forbiddenFramingHeaders = setOf(
        "content-length",
        "connection",
        "proxy-connection",
        "transfer-encoding",
        "upgrade",
        "te",
    )

    data class ValidatedRequest(
        val normalizedUrl: String,
        val methodName: String,
        val headers: List<ParsedHeader>,
        val manualCookies: Map<String, String>,
        val bodyBytes: ByteArray?,
        val contentType: String?,
    )

    fun validate(draft: RequestDraft): Result<ValidatedRequest> = runCatching {
        val rawUrl = draft.url.trim()
        require(rawUrl.isNotBlank()) { "La URL es obligatoria." }
        require(rawUrl.length <= MAX_URL_LENGTH) { "La URL supera $MAX_URL_LENGTH caracteres." }

        val url = try {
            URI(rawUrl)
        } catch (_: URISyntaxException) {
            error("La URL no es válida. Usa una URL HTTP o HTTPS completa.")
        }

        require(url.isAbsolute && url.host != null) {
            "La URL no es válida. Usa una URL HTTP o HTTPS completa."
        }
        val scheme = url.scheme.lowercase()
        require(scheme == "https" || scheme == "http") { "Solo se admiten los esquemas HTTP y HTTPS." }
        require(scheme != "http" || draft.allowCleartext) {
            "HTTP sin cifrar requiere activar explícitamente el modo de laboratorio."
        }
        require(url.userInfo == null) {
            "No incluyas credenciales en la URL. Usa Authorization o el editor de headers."
        }
        require(url.rawFragment == null) {
            "El fragmento #... no forma parte de una petición HTTP y debe eliminarse."
        }
        require(draft.timeoutSeconds in 1..120) { "El timeout debe estar entre 1 y 120 segundos." }

        val methodName = when (draft.method) {
            com.imr.reqestra.domain.model.HttpMethodOption.CUSTOM -> draft.customMethod.trim().uppercase()
            else -> draft.method.wireName
        }
        require(tokenRegex.matches(methodName)) { "El método HTTP personalizado no es válido." }

        val effectiveBodyPolicy = when (methodName) {
            "GET", "HEAD" -> BodyPolicy.FORBIDDEN
            "POST", "PUT", "PATCH", "PROPPATCH", "REPORT" -> BodyPolicy.REQUIRED
            else -> draft.method.bodyPolicy
        }
        val bodyBytes = buildBody(draft, effectiveBodyPolicy)
        if (effectiveBodyPolicy == BodyPolicy.FORBIDDEN) {
            require(bodyBytes == null) { "$methodName no admite cuerpo en este cliente." }
        }
        if (effectiveBodyPolicy == BodyPolicy.REQUIRED && bodyBytes == null) {
            error("$methodName requiere un cuerpo. Selecciona un tipo de cuerpo o usa uno vacío.")
        }

        val contentType = if (bodyBytes != null) {
            draft.contentType.trim().ifBlank { draft.bodyMode.defaultContentType }
                .also {
                    require(!it.contains('\r') && !it.contains('\n')) {
                        "Content-Type contiene caracteres no permitidos."
                    }
                }
        } else null

        ValidatedRequest(
            normalizedUrl = url.toASCIIString(),
            methodName = methodName,
            headers = parseHeaders(draft.headersText),
            manualCookies = parseCookies(draft.cookiesText),
            bodyBytes = bodyBytes,
            contentType = contentType,
        )
    }

    fun parseHeaders(raw: String): List<ParsedHeader> {
        require(!raw.contains('\r')) { "Los headers deben usar saltos de línea LF, no CR." }
        val lines = raw.lineSequence()
            .map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .toList()

        require(lines.size <= MAX_HEADER_COUNT) { "Se permiten como máximo $MAX_HEADER_COUNT headers." }

        return lines.mapIndexed { index, line ->
            val separator = line.indexOf(':')
            require(separator > 0) { "Header inválido en la línea ${index + 1}. Usa Nombre: Valor." }

            val name = line.substring(0, separator).trim()
            val value = line.substring(separator + 1).trim()

            require(tokenRegex.matches(name)) { "Nombre de header inválido en la línea ${index + 1}." }
            require(name.lowercase() !in forbiddenFramingHeaders) {
                "$name lo administra OkHttp para mantener un framing HTTP coherente."
            }
            require(value.length <= MAX_HEADER_VALUE_LENGTH) {
                "El valor de $name supera $MAX_HEADER_VALUE_LENGTH caracteres."
            }
            require(!value.contains('\r') && !value.contains('\n')) {
                "El valor de $name contiene saltos de línea no permitidos."
            }
            ParsedHeader(name, value)
        }
    }

    fun mergeHeaders(defaults: List<ParsedHeader>, overrides: List<ParsedHeader>): List<ParsedHeader> {
        val overriddenNames = overrides.map { it.name.lowercase() }.toSet()
        return defaults.filterNot { it.name.lowercase() in overriddenNames } + overrides
    }

    fun parseCookies(raw: String): Map<String, String> {
        if (raw.isBlank()) return emptyMap()
        val entries = raw.lineSequence()
            .flatMap { line -> line.split(';').asSequence() }
            .map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .toList()

        require(entries.size <= 100) { "Se permiten como máximo 100 cookies manuales." }
        val result = linkedMapOf<String, String>()
        entries.forEachIndexed { index, entry ->
            val separator = entry.indexOf('=')
            require(separator > 0) { "Cookie inválida en la entrada ${index + 1}. Usa nombre=valor." }
            val name = entry.substring(0, separator).trim()
            val value = entry.substring(separator + 1).trim()
            require(tokenRegex.matches(name)) { "Nombre de cookie inválido: $name." }
            require(value.none { it == '\r' || it == '\n' || it == ';' || it.code < 0x20 }) {
                "La cookie $name contiene caracteres no permitidos."
            }
            result[name] = value
        }
        return result
    }

    fun parseCookieHeader(headers: List<ParsedHeader>): Map<String, String> = headers
        .filter { it.name.equals("Cookie", ignoreCase = true) }
        .flatMap { it.value.split(';') }
        .joinToString(";")
        .let(::parseCookies)

    private fun buildBody(draft: RequestDraft, policy: BodyPolicy): ByteArray? {
        if (draft.bodyMode == BodyMode.NONE) {
            return if (policy == BodyPolicy.REQUIRED) ByteArray(0) else null
        }
        val bytes = when (draft.bodyMode) {
            BodyMode.BASE64_BINARY -> try {
                Base64.getDecoder().decode(draft.body.filterNot(Char::isWhitespace))
            } catch (_: IllegalArgumentException) {
                error("El cuerpo no es Base64 válido.")
            }
            else -> draft.body.toByteArray(Charsets.UTF_8)
        }
        require(bytes.size <= MAX_BODY_BYTES) { "El cuerpo supera el límite de 5 MiB." }
        return bytes
    }
}
