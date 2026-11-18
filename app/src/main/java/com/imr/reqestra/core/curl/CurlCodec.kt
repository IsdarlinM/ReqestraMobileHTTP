package com.imr.reqestra.core.curl

import com.imr.reqestra.core.security.SensitiveDataRedactor
import com.imr.reqestra.core.validation.RequestValidator
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.RequestDraft

/** Importa comandos curl POSIX como datos: nunca invoca shell ni lee archivos locales. */
object CurlCodec {
    private const val MAX_COMMAND_CHARS = 6 * 1024 * 1024

    /** Omite cookies, headers de sesión y contenido real del cuerpo al compartir. */
    fun exportSanitized(draft: RequestDraft): Result<String> = runCatching {
        val validated = RequestValidator.validate(draft).getOrThrow()
        val safeUrl = SensitiveDataRedactor.redactUrl(validated.normalizedUrl).let { normalized ->
            val question = normalized.indexOf('?')
            if (question < 0) normalized else normalized.substring(0, question + 1) +
                normalized.substring(question + 1).split('&').joinToString("&") { pair ->
                    if (pair.contains('=')) "${pair.substringBefore('=')}=REDACTED" else pair
                }
        }
        val parts = mutableListOf(
            "curl", "--request", quote(validated.methodName),
            "--url", quote(safeUrl),
        )
        validated.headers.forEach { header ->
            // Una cabecera arbitraria también puede transportar secretos.
            val safeValue = if (header.name.equals("Content-Type", true) ||
                header.name.equals("Accept", true) || header.name.equals("Accept-Language", true)) {
                header.value
            } else "REDACTED"
            parts += "--header"
            parts += quote("${header.name}: $safeValue")
        }
        validated.contentType?.let { contentType ->
            if (validated.headers.none { it.name.equals("Content-Type", true) }) {
                parts += "--header"
                parts += quote("Content-Type: $contentType")
            }
        }
        if (validated.bodyBytes != null && validated.bodyBytes.isNotEmpty()) {
            parts += "--data-raw"
            parts += quote("[REDACTED BODY]")
        }
        if (draft.followRedirects) parts += "--location"
        if (draft.forceHttp1) parts += "--http1.1"
        parts += "--max-time"
        parts += draft.timeoutSeconds.toString()
        parts.joinToString(" ")
    }

    /** No interpreta operaciones de shell, expansión de variables ni opciones inseguras de TLS. */
    fun importCommand(raw: String): Result<RequestDraft> = runCatching {
        require(raw.length <= MAX_COMMAND_CHARS) { "El comando supera el tamaño máximo permitido." }
        val tokens = splitPosix(raw)
        require(tokens.firstOrNull() == "curl") { "Pega un comando que comience por curl." }
        require(tokens.size <= 256) { "Demasiadas opciones en el comando." }

        var url: String? = null
        var method: String? = null
        var contentType: String? = null
        var body: String? = null
        var redirects = false
        var http1 = false
        var timeout = 30
        val headers = mutableListOf<String>()
        val cookies = mutableListOf<String>()
        var index = 1
        fun nextArgument(flag: String): String {
            require(index + 1 < tokens.size) { "Falta un valor para $flag." }
            return tokens[++index]
        }
        while (index < tokens.size) {
            val token = tokens[index]
            val value: String? = when {
                token in setOf("--request", "-X", "--url", "--header", "-H", "--cookie", "-b", "--data", "-d", "--data-raw", "--data-binary", "--max-time") -> nextArgument(token)
                token.startsWith("--request=") || token.startsWith("--url=") ||
                    token.startsWith("--header=") || token.startsWith("--cookie=") ||
                    token.startsWith("--data=") || token.startsWith("--data-raw=") ||
                    token.startsWith("--data-binary=") || token.startsWith("--max-time=") -> token.substringAfter('=')
                token.startsWith("-X") && token.length > 2 -> token.drop(2)
                token.startsWith("-H") && token.length > 2 -> token.drop(2)
                token.startsWith("-d") && token.length > 2 -> token.drop(2)
                else -> null
            }
            val name = when {
                token.startsWith("--") -> token.substringBefore('=')
                token.startsWith("-X") -> "-X"
                token.startsWith("-H") -> "-H"
                token.startsWith("-d") -> "-d"
                else -> token
            }
            when (name) {
                "-X", "--request" -> method = value
                "--url" -> url = value
                "-H", "--header" -> {
                    require(value != null && value.contains(':')) { "Header inválido." }
                    if (value.substringBefore(':').trim().equals("Content-Type", true)) {
                        contentType = value.substringAfter(':').trim()
                    } else headers += value
                }
                "-b", "--cookie" -> {
                    require(value != null && value.contains('=')) { "Solo se admiten cookies inline; no archivos locales." }
                    cookies += value
                }
                "-d", "--data", "--data-raw", "--data-binary" -> {
                    require(value != null) { "Cuerpo vacío inesperado." }
                    require(name == "--data-raw" || !value.startsWith("@")) {
                        "No se admite leer archivos (@ruta) en la importación."
                    }
                    body = if (body == null) value else "$body&$value"
                }
                "--location", "-L" -> redirects = true
                "--http1.1" -> http1 = true
                "--head", "-I" -> method = "HEAD"
                "--max-time" -> timeout = value?.toIntOrNull()
                    ?: throw IllegalArgumentException("Timeout inválido.")
                "--compressed", "--silent", "--show-error", "-s", "-S", "-sS", "--fail", "-f" -> Unit
                "--insecure", "-k" -> error("No se importa --insecure: Reqestra valida TLS.")
                else -> {
                    require(!token.startsWith('-')) { "Opción cURL no compatible: $token" }
                    require(url == null) { "El comando contiene varias URLs." }
                    url = token
                }
            }
            index++
        }
        require(!url.isNullOrBlank()) { "El comando no contiene una URL." }
        require(timeout in 1..120) { "El timeout debe estar entre 1 y 120 segundos." }
        require(headers.size <= 100) { "Demasiados headers." }
        val wireMethod = (method ?: if (body != null) "POST" else "GET").uppercase()
        val methodOption = HttpMethodOption.entries.firstOrNull { it.wireName == wireMethod }
            ?: HttpMethodOption.CUSTOM
        val bodyMode = when {
            body == null -> BodyMode.NONE
            contentType?.contains("json", ignoreCase = true) == true -> BodyMode.JSON
            contentType?.contains("xml", ignoreCase = true) == true -> BodyMode.XML
            else -> BodyMode.RAW
        }
        val result = RequestDraft(
            url = url,
            method = methodOption,
            customMethod = if (methodOption == HttpMethodOption.CUSTOM) wireMethod else "PROPFIND",
            headersText = headers.joinToString("\n"),
            cookiesText = cookies.joinToString("\n"),
            bodyMode = bodyMode,
            body = body.orEmpty(),
            contentType = contentType ?: if (body != null) "application/x-www-form-urlencoded" else "",
            followRedirects = redirects,
            forceHttp1 = http1,
            timeoutSeconds = timeout,
            // HTTP nunca se habilita automáticamente: el usuario debe aprobarlo en Opciones.
            allowCleartext = false,
        )
        RequestValidator.parseHeaders(result.headersText)
        RequestValidator.parseCookies(result.cookiesText)
        result
    }

    private fun quote(text: String): String = "'" + text.replace("'", "'\"'\"'") + "'"

    private fun splitPosix(input: String): List<String> {
        val tokens = mutableListOf<String>()
        val current = StringBuilder()
        var quote: Char? = null
        var escaped = false
        var started = false
        for (c in input) {
            if (escaped) {
                if (c != '\n') current.append(c)
                escaped = false
                started = true
                continue
            }
            when {
                c == '\\' && quote != '\'' -> { escaped = true; started = true }
                quote != null -> {
                    if (c == quote) quote = null else current.append(c)
                }
                c == '\'' || c == '"' -> { quote = c; started = true }
                c.isWhitespace() -> if (started) {
                    tokens += current.toString()
                    current.clear()
                    started = false
                }
                else -> { current.append(c); started = true }
            }
        }
        require(!escaped && quote == null) { "El comando tiene comillas o escapes sin cerrar." }
        if (started) tokens += current.toString()
        return tokens
    }
}
