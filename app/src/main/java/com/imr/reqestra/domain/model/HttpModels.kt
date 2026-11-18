package com.imr.reqestra.domain.model

enum class HttpMethodOption(
    val wireName: String,
    val bodyPolicy: BodyPolicy,
) {
    GET("GET", BodyPolicy.FORBIDDEN),
    POST("POST", BodyPolicy.REQUIRED),
    PUT("PUT", BodyPolicy.REQUIRED),
    PATCH("PATCH", BodyPolicy.REQUIRED),
    DELETE("DELETE", BodyPolicy.OPTIONAL),
    HEAD("HEAD", BodyPolicy.FORBIDDEN),
    OPTIONS("OPTIONS", BodyPolicy.OPTIONAL),
    CUSTOM("CUSTOM", BodyPolicy.OPTIONAL),
}

enum class BodyPolicy { FORBIDDEN, OPTIONAL, REQUIRED }

enum class BodyMode(
    val label: String,
    val defaultContentType: String,
) {
    NONE("Sin cuerpo", ""),
    RAW("Texto / Raw", "text/plain; charset=utf-8"),
    JSON("JSON", "application/json; charset=utf-8"),
    HTML("HTML", "text/html; charset=utf-8"),
    XML("XML", "application/xml; charset=utf-8"),
    FORM_URLENCODED("Form URL encoded", "application/x-www-form-urlencoded; charset=utf-8"),
    GRAPHQL("GraphQL", "application/json; charset=utf-8"),
    BASE64_BINARY("Binario Base64", "application/octet-stream"),
}

enum class SessionMode(val label: String) {
    NONE("Sin sesión"),
    MEMORY("Solo memoria"),
    PERSISTENT("Persistente cifrada"),
}

data class ParsedHeader(
    val name: String,
    val value: String,
)

data class RequestDraft(
    val url: String = "https://example.com",
    val method: HttpMethodOption = HttpMethodOption.GET,
    val customMethod: String = "PROPFIND",
    val headersText: String = "Accept: */*",
    val cookiesText: String = "",
    val bodyMode: BodyMode = BodyMode.NONE,
    val contentType: String = BodyMode.NONE.defaultContentType,
    val body: String = "",
    val sessionMode: SessionMode = SessionMode.NONE,
    val sessionHeadersText: String = "",
    val followRedirects: Boolean = false,
    val allowCleartext: Boolean = false,
    val forceHttp1: Boolean = false,
    val timeoutSeconds: Int = 30,
    val queryParamsText: String = "",
)

data class RequestSnapshot(
    val method: String,
    val url: String,
    val headers: List<ParsedHeader>,
    val body: String,
    val bodyMode: BodyMode,
    val bodyBytes: Int,
)

data class ResponseSnapshot(
    val request: RequestSnapshot,
    val statusCode: Int,
    val statusMessage: String,
    val protocol: String,
    val durationMs: Long,
    val headers: List<ParsedHeader>,
    val body: String,
    val bodyTruncated: Boolean,
    val contentType: String?,
    val receivedAtEpochMs: Long,
    val receivedCookies: Int,
)

data class HistoryEntry(
    val id: Long,
    val method: String,
    val url: String,
    val statusCode: Int,
    val durationMs: Long,
    val timestampEpochMs: Long,
)

data class SessionCookieInfo(
    val name: String,
    val domain: String,
    val path: String,
    val secure: Boolean,
    val persistent: Boolean,
)

data class SessionSummary(
    val mode: SessionMode,
    val headerCount: Int,
    val cookieCount: Int,
    val cookies: List<SessionCookieInfo>,
)

sealed interface OperationResult<out T> {
    data class Success<T>(val value: T, val notice: String? = null) : OperationResult<T>
    data class Failure(val message: String) : OperationResult<Nothing>
}

const val DEFAULT_BODY = """<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Reqestra</title>
  <style>
    body { font-family: system-ui, sans-serif; margin: 2rem; background: #07111f; color: #e7fff9; }
    main { max-width: 680px; margin: auto; padding: 2rem; border: 1px solid #17d9b0; border-radius: 18px; }
    code { color: #17d9b0; }
  </style>
</head>
<body>
  <main>
    <h1>Reqestra</h1>
    <p>Edit method, URL, query, headers, cookies, body and transport options.</p>
    <code>imr :: v3.5.0</code>
  </main>
</body>
</html>"""
