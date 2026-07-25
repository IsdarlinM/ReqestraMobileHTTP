package com.imr.reqestra.data.network

import com.imr.reqestra.core.request.RequestComposer
import com.imr.reqestra.core.security.SensitiveDataRedactor
import com.imr.reqestra.core.validation.RequestValidator
import com.imr.reqestra.data.session.SessionRepository
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.OperationResult
import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.RequestDraft
import com.imr.reqestra.domain.model.RequestSnapshot
import com.imr.reqestra.domain.model.ResponseSnapshot
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import okhttp3.Call
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class HttpEngine(
    private val sessionRepository: SessionRepository,
    private val baseClient: OkHttpClient = defaultClient(),
) {
    private val activeCall = AtomicReference<Call?>(null)

    suspend fun execute(draft: RequestDraft): OperationResult<ResponseSnapshot> {
        val validated = RequestValidator.validate(draft).getOrElse {
            return OperationResult.Failure(it.message ?: "La petición no es válida.")
        }

        val url = validated.normalizedUrl.toHttpUrl()
        val sessionHeaders = sessionRepository.headers(url, draft.sessionMode)
        val composed = RequestComposer.compose(
            sessionHeaders = sessionHeaders,
            requestHeaders = validated.headers,
            sessionCookies = emptyMap(),
            manualCookies = validated.manualCookies,
        )
        var finalHeaders = composed.headers.filterNot { it.name.equals("Cookie", ignoreCase = true) }
        val requestCookies = composed.cookies

        if (draft.sessionMode != com.imr.reqestra.domain.model.SessionMode.NONE) {
            sessionRepository.seedCookies(url, draft.sessionMode, requestCookies)
        }
        val cookieJar = RequestSessionCookieJar(
            repository = sessionRepository,
            mode = draft.sessionMode,
            initialUrl = url,
            requestOverrides = requestCookies,
        )

        val clientBuilder = baseClient.newBuilder()
            .cookieJar(cookieJar)
            .followRedirects(draft.followRedirects)
            .followSslRedirects(draft.followRedirects)
            .callTimeout(draft.timeoutSeconds.toLong(), TimeUnit.SECONDS)
        if (draft.forceHttp1) clientBuilder.protocols(listOf(Protocol.HTTP_1_1))
        val client = clientBuilder.build()

        val requestBuilder = Request.Builder()
            .url(url)
            .headers(finalHeaders.toOkHttpHeaders())

        if (finalHeaders.none { it.name.equals("User-Agent", ignoreCase = true) }) {
            requestBuilder.header("User-Agent", "Reqestra/2.0.0 Android")
            finalHeaders = finalHeaders + ParsedHeader("User-Agent", "Reqestra/2.0.0 Android")
        }

        val mediaType = validated.contentType?.toMediaTypeOrNull()
        val requestBody = validated.bodyBytes?.toRequestBody(mediaType)
        requestBuilder.method(validated.methodName, requestBody)

        val snapshotCookies = cookieJar.loadForRequest(url)
        val snapshotHeaders = if (snapshotCookies.isEmpty()) {
            finalHeaders
        } else {
            finalHeaders + ParsedHeader(
                "Cookie",
                snapshotCookies.joinToString("; ") { "${it.name}=${it.value}" },
            )
        }
        val requestSnapshot = RequestSnapshot(
            method = validated.methodName,
            url = SensitiveDataRedactor.redactUrl(validated.normalizedUrl),
            headers = SensitiveDataRedactor.redact(snapshotHeaders),
            body = if (draft.bodyMode == BodyMode.BASE64_BINARY) "[${validated.bodyBytes?.size ?: 0} bytes binarios desde Base64]" else draft.body,
            bodyMode = draft.bodyMode,
            bodyBytes = validated.bodyBytes?.size ?: 0,
        )

        val call = client.newCall(requestBuilder.build())
        activeCall.set(call)
        val startedAt = System.nanoTime()

        return try {
            call.execute().use { response ->
                val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)
                val receivedCookies = cookieJar.receivedCookieCount

                val contentTypeHeader = response.body?.contentType()
                    ?: response.header("Content-Type")?.toMediaTypeOrNull()
                val charset = contentTypeHeader?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
                val limitedBody = response.body?.byteStream()?.readLimited(MAX_RESPONSE_BYTES)
                    ?: LimitedBytes(ByteArray(0), false)

                val responseHeaders = response.headers.names().flatMap { name ->
                    response.headers.values(name).map { value -> ParsedHeader(name, value) }
                }

                OperationResult.Success(
                    ResponseSnapshot(
                        request = requestSnapshot,
                        statusCode = response.code,
                        statusMessage = response.message,
                        protocol = response.protocol.toString(),
                        durationMs = durationMs,
                        headers = SensitiveDataRedactor.redact(responseHeaders),
                        body = limitedBody.bytes.toString(charset.safeCharset()),
                        bodyTruncated = limitedBody.truncated,
                        contentType = contentTypeHeader?.toString(),
                        receivedAtEpochMs = System.currentTimeMillis(),
                        receivedCookies = receivedCookies,
                    ),
                    notice = buildList {
                        if (receivedCookies > 0 && draft.sessionMode != com.imr.reqestra.domain.model.SessionMode.NONE) {
                            add("Sesión actualizada con $receivedCookies cookie(s).")
                        }
                        if (limitedBody.truncated) add("La respuesta se truncó a 4 MiB.")
                    }.joinToString(" ").ifBlank { null },
                )
            }
        } catch (error: IOException) {
            val message = if (call.isCanceled()) "La petición fue cancelada." else friendlyNetworkError(error)
            OperationResult.Failure(message)
        } catch (unexpected: Exception) {
            OperationResult.Failure(unexpected.message ?: "Error inesperado al ejecutar la petición.")
        } finally {
            activeCall.compareAndSet(call, null)
        }
    }

    fun cancelActive() {
        activeCall.getAndSet(null)?.cancel()
    }

    private fun friendlyNetworkError(error: IOException): String {
        val raw = error.message.orEmpty()
        return when {
            raw.contains("CLEARTEXT", ignoreCase = true) -> "HTTP sin cifrar está bloqueado por Android o no fue autorizado."
            raw.contains("certificate", ignoreCase = true) || raw.contains("SSL", ignoreCase = true) ->
                "No se pudo validar el certificado TLS del servidor."
            raw.contains("timeout", ignoreCase = true) -> "La petición agotó el tiempo de espera."
            raw.isNotBlank() -> raw
            else -> "No fue posible conectar con el servidor."
        }
    }

    private class RequestSessionCookieJar(
        private val repository: SessionRepository,
        private val mode: com.imr.reqestra.domain.model.SessionMode,
        initialUrl: okhttp3.HttpUrl,
        requestOverrides: Map<String, String>,
    ) : CookieJar {
        private val lock = Any()
        private val transientCookies = mutableListOf<Cookie>()
        var receivedCookieCount: Int = 0
            private set

        init {
            requestOverrides.forEach { (name, value) ->
                val builder = Cookie.Builder()
                    .name(name)
                    .value(value)
                    .hostOnlyDomain(initialUrl.host)
                    .path(defaultCookiePath(initialUrl.encodedPath))
                if (initialUrl.isHttps) builder.secure()
                transientCookies += builder.build()
            }
        }

        override fun loadForRequest(url: okhttp3.HttpUrl): List<Cookie> = synchronized(lock) {
            removeExpired(transientCookies)
            val merged = linkedMapOf<String, Cookie>()
            repository.cookiesFor(url, mode).forEach { cookie ->
                merged[cookie.identity()] = cookie
            }
            transientCookies.filter { it.matches(url) }.forEach { cookie ->
                merged[cookie.identity()] = cookie
            }
            merged.values.toList()
        }

        override fun saveFromResponse(url: okhttp3.HttpUrl, cookies: List<Cookie>) = synchronized(lock) {
            if (cookies.isEmpty()) return@synchronized
            receivedCookieCount += cookies.size
            cookies.forEach { cookie ->
                transientCookies.removeAll { it.identity() == cookie.identity() }
                if (cookie.expiresAt > System.currentTimeMillis()) transientCookies += cookie
            }
            removeExpired(transientCookies)
            repository.saveResponseCookies(mode, cookies)
        }

        private fun Cookie.identity(): String = "$name|$domain|$path"

        private fun removeExpired(source: MutableList<Cookie>) {
            val now = System.currentTimeMillis()
            source.removeAll { it.expiresAt <= now }
        }

        private fun defaultCookiePath(encodedPath: String): String {
            if (!encodedPath.startsWith('/') || encodedPath == "/") return "/"
            val lastSlash = encodedPath.lastIndexOf('/')
            return if (lastSlash <= 0) "/" else encodedPath.substring(0, lastSlash)
        }
    }

    private data class LimitedBytes(val bytes: ByteArray, val truncated: Boolean)

    private fun java.io.InputStream.readLimited(limit: Int): LimitedBytes {
        val output = ByteArrayOutputStream(minOf(limit, 64 * 1024))
        val buffer = ByteArray(8 * 1024)
        var total = 0
        var truncated = false
        while (true) {
            val read = read(buffer)
            if (read == -1) break
            val remaining = limit - total
            if (remaining <= 0) {
                truncated = true
                break
            }
            val toWrite = minOf(read, remaining)
            output.write(buffer, 0, toWrite)
            total += toWrite
            if (toWrite < read) {
                truncated = true
                break
            }
        }
        return LimitedBytes(output.toByteArray(), truncated)
    }

    private fun Charset.safeCharset(): Charset = runCatching { Charset.forName(name()) }
        .getOrDefault(Charsets.UTF_8)

    private fun List<ParsedHeader>.toOkHttpHeaders(): Headers {
        val builder = Headers.Builder()
        forEach { builder.add(it.name, it.value) }
        return builder.build()
    }

    companion object {
        private const val MAX_RESPONSE_BYTES = 4 * 1024 * 1024

        private fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .followRedirects(false)
            .followSslRedirects(false)
            .build()
    }
}
