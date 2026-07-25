package com.imr.reqestra.data.session

import android.content.Context
import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.SessionCookieInfo
import com.imr.reqestra.domain.model.SessionMode
import com.imr.reqestra.domain.model.SessionSummary
import okhttp3.Cookie
import okhttp3.HttpUrl
import org.json.JSONArray
import org.json.JSONObject

class AndroidSessionRepository(context: Context) : SessionRepository {
    private val securePreferences = SecurePreferences(context.applicationContext)
    private val lock = Any()
    private val memoryHeaders = mutableMapOf<String, List<ParsedHeader>>()
    private val memoryCookies = mutableListOf<Cookie>()
    private var persistentHeaders: MutableMap<String, List<ParsedHeader>>? = null
    private var persistentCookies: MutableList<Cookie>? = null

    override fun preferredMode(): SessionMode = securePreferences.getString(KEY_MODE)
        ?.let { runCatching { SessionMode.valueOf(it) }.getOrNull() }
        ?: SessionMode.NONE

    override fun setPreferredMode(mode: SessionMode) {
        securePreferences.putString(KEY_MODE, mode.name)
    }

    override fun headers(url: HttpUrl, mode: SessionMode): List<ParsedHeader> = synchronized(lock) {
        val key = originKey(url)
        when (mode) {
            SessionMode.NONE -> emptyList()
            SessionMode.MEMORY -> memoryHeaders[key].orEmpty()
            SessionMode.PERSISTENT -> loadPersistentHeaders()[key].orEmpty()
        }
    }

    override fun updateHeaders(url: HttpUrl, mode: SessionMode, headers: List<ParsedHeader>) = synchronized(lock) {
        val key = originKey(url)
        when (mode) {
            SessionMode.NONE -> Unit
            SessionMode.MEMORY -> memoryHeaders[key] = headers
            SessionMode.PERSISTENT -> {
                val allHeaders = loadPersistentHeaders()
                allHeaders[key] = headers
                securePreferences.putString(KEY_HEADERS, serializeHeadersByOrigin(allHeaders))
            }
        }
    }

    override fun cookiesFor(url: HttpUrl, mode: SessionMode): List<Cookie> = synchronized(lock) {
        val source = cookieList(mode)
        removeExpired(source)
        persistCookiesIfNeeded(mode, source)
        source.filter { it.matches(url) }
    }

    override fun seedCookies(
        url: HttpUrl,
        mode: SessionMode,
        cookies: Map<String, String>,
    ) = synchronized(lock) {
        if (mode == SessionMode.NONE || cookies.isEmpty()) return@synchronized
        val source = cookieList(mode)
        cookies.forEach { (name, value) ->
            val builder = Cookie.Builder()
                .name(name)
                .value(value)
                .hostOnlyDomain(url.host)
                .path(defaultCookiePath(url.encodedPath))
            if (url.isHttps) builder.secure()
            upsert(source, builder.build())
        }
        persistCookiesIfNeeded(mode, source)
    }

    override fun saveResponseCookies(mode: SessionMode, cookies: List<Cookie>) = synchronized(lock) {
        if (mode == SessionMode.NONE || cookies.isEmpty()) return@synchronized
        val source = cookieList(mode)
        cookies.forEach { cookie ->
            removeIdentity(source, cookie)
            if (cookie.expiresAt > System.currentTimeMillis()) source += cookie
        }
        removeExpired(source)
        persistCookiesIfNeeded(mode, source)
    }

    override fun clear(mode: SessionMode) = synchronized(lock) {
        when (mode) {
            SessionMode.NONE -> Unit
            SessionMode.MEMORY -> {
                memoryHeaders.clear()
                memoryCookies.clear()
            }
            SessionMode.PERSISTENT -> {
                persistentHeaders = mutableMapOf()
                persistentCookies = mutableListOf()
                securePreferences.remove(KEY_HEADERS)
                securePreferences.remove(KEY_COOKIES)
            }
        }
    }

    override fun clearAll() = synchronized(lock) {
        memoryHeaders.clear()
        memoryCookies.clear()
        persistentHeaders = mutableMapOf()
        persistentCookies = mutableListOf()
        securePreferences.clear()
    }

    override fun summary(url: HttpUrl, mode: SessionMode): SessionSummary = synchronized(lock) {
        val currentHeaders = headers(url, mode)
        val cookies = cookieList(mode)
        removeExpired(cookies)
        persistCookiesIfNeeded(mode, cookies)
        SessionSummary(
            mode = mode,
            headerCount = currentHeaders.size,
            cookieCount = cookies.size,
            cookies = cookies
                .sortedWith(compareBy<Cookie> { it.domain }.thenBy { it.path }.thenBy { it.name })
                .map {
                    SessionCookieInfo(
                        name = it.name,
                        domain = it.domain,
                        path = it.path,
                        secure = it.secure,
                        persistent = it.persistent,
                    )
                },
        )
    }

    private fun cookieList(mode: SessionMode): MutableList<Cookie> = when (mode) {
        SessionMode.NONE -> mutableListOf()
        SessionMode.MEMORY -> memoryCookies
        SessionMode.PERSISTENT -> loadPersistentCookies()
    }

    private fun loadPersistentHeaders(): MutableMap<String, List<ParsedHeader>> {
        persistentHeaders?.let { return it }
        val loaded = securePreferences.getString(KEY_HEADERS)
            ?.let(::deserializeHeadersByOrigin)
            ?.toMutableMap()
            ?: mutableMapOf()
        persistentHeaders = loaded
        return loaded
    }

    private fun loadPersistentCookies(): MutableList<Cookie> {
        persistentCookies?.let { return it }
        val loaded = securePreferences.getString(KEY_COOKIES)
            ?.let(::deserializeCookies)
            ?.toMutableList()
            ?: mutableListOf()
        removeExpired(loaded)
        persistentCookies = loaded
        return loaded
    }

    private fun persistCookiesIfNeeded(mode: SessionMode, cookies: List<Cookie>) {
        if (mode == SessionMode.PERSISTENT) {
            securePreferences.putString(KEY_COOKIES, serializeCookies(cookies))
        }
    }

    private fun upsert(source: MutableList<Cookie>, cookie: Cookie) {
        removeIdentity(source, cookie)
        source += cookie
    }

    private fun removeIdentity(source: MutableList<Cookie>, cookie: Cookie) {
        source.removeAll {
            it.name == cookie.name && it.domain == cookie.domain && it.path == cookie.path
        }
    }

    private fun removeExpired(source: MutableList<Cookie>) {
        val now = System.currentTimeMillis()
        source.removeAll { it.expiresAt <= now }
    }

    private fun serializeHeadersByOrigin(headers: Map<String, List<ParsedHeader>>): String = JSONArray().apply {
        headers.toSortedMap().forEach { (origin, values) ->
            put(
                JSONObject()
                    .put("origin", origin)
                    .put(
                        "headers",
                        JSONArray().apply {
                            values.forEach { header ->
                                put(JSONObject().put("name", header.name).put("value", header.value))
                            }
                        },
                    ),
            )
        }
    }.toString()

    private fun deserializeHeadersByOrigin(raw: String): Map<String, List<ParsedHeader>> = runCatching {
        val array = JSONArray(raw)
        buildMap {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val headersArray = item.getJSONArray("headers")
                val values = buildList {
                    for (headerIndex in 0 until headersArray.length()) {
                        val header = headersArray.getJSONObject(headerIndex)
                        add(ParsedHeader(header.getString("name"), header.getString("value")))
                    }
                }
                put(item.getString("origin"), values)
            }
        }
    }.getOrDefault(emptyMap())

    private fun serializeCookies(cookies: List<Cookie>): String = JSONArray().apply {
        cookies.forEach { cookie ->
            put(
                JSONObject()
                    .put("name", cookie.name)
                    .put("value", cookie.value)
                    .put("expiresAt", cookie.expiresAt)
                    .put("domain", cookie.domain)
                    .put("path", cookie.path)
                    .put("secure", cookie.secure)
                    .put("httpOnly", cookie.httpOnly)
                    .put("hostOnly", cookie.hostOnly),
            )
        }
    }.toString()

    private fun deserializeCookies(raw: String): List<Cookie> = runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val builder = Cookie.Builder()
                    .name(item.getString("name"))
                    .value(item.getString("value"))
                    .path(item.getString("path"))
                if (item.optBoolean("hostOnly", false)) {
                    builder.hostOnlyDomain(item.getString("domain"))
                } else {
                    builder.domain(item.getString("domain"))
                }
                if (item.optBoolean("secure", false)) builder.secure()
                if (item.optBoolean("httpOnly", false)) builder.httpOnly()
                val expiresAt = item.optLong("expiresAt", Long.MAX_VALUE)
                if (expiresAt != Long.MAX_VALUE) builder.expiresAt(expiresAt)
                add(builder.build())
            }
        }
    }.getOrDefault(emptyList())

    private fun originKey(url: HttpUrl): String = "${url.scheme.lowercase()}://${url.host.lowercase()}:${url.port}"

    private fun defaultCookiePath(encodedPath: String): String {
        if (!encodedPath.startsWith('/') || encodedPath == "/") return "/"
        val lastSlash = encodedPath.lastIndexOf('/')
        return if (lastSlash <= 0) "/" else encodedPath.substring(0, lastSlash)
    }

    private companion object {
        const val KEY_MODE = "preferred_mode"
        const val KEY_HEADERS = "persistent_headers_by_origin"
        const val KEY_COOKIES = "persistent_cookies"
    }
}
