package com.imr.reqestra.data.session

import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.SessionCookieInfo
import com.imr.reqestra.domain.model.SessionMode
import com.imr.reqestra.domain.model.SessionSummary
import okhttp3.Cookie
import okhttp3.HttpUrl

class InMemorySessionRepository : SessionRepository {
    private var mode: SessionMode = SessionMode.NONE
    private val headers = mutableMapOf<SessionMode, MutableMap<String, List<ParsedHeader>>>()
    private val cookies = mutableMapOf<SessionMode, MutableList<Cookie>>()

    override fun preferredMode(): SessionMode = mode

    override fun setPreferredMode(mode: SessionMode) {
        this.mode = mode
    }

    override fun headers(url: HttpUrl, mode: SessionMode): List<ParsedHeader> =
        headers[mode]?.get(originKey(url)).orEmpty()

    override fun updateHeaders(url: HttpUrl, mode: SessionMode, headers: List<ParsedHeader>) {
        if (mode == SessionMode.NONE) return
        this.headers.getOrPut(mode) { mutableMapOf() }[originKey(url)] = headers
    }

    override fun cookiesFor(url: HttpUrl, mode: SessionMode): List<Cookie> =
        cookies[mode].orEmpty().filter { it.expiresAt > System.currentTimeMillis() && it.matches(url) }

    override fun seedCookies(url: HttpUrl, mode: SessionMode, cookies: Map<String, String>) {
        if (mode == SessionMode.NONE) return
        val target = this.cookies.getOrPut(mode) { mutableListOf() }
        cookies.forEach { (name, value) ->
            target.removeAll { it.name == name && it.domain == url.host && it.path == defaultCookiePath(url.encodedPath) }
            val builder = Cookie.Builder()
                .name(name)
                .value(value)
                .hostOnlyDomain(url.host)
                .path(defaultCookiePath(url.encodedPath))
            if (url.isHttps) builder.secure()
            target += builder.build()
        }
    }

    override fun saveResponseCookies(mode: SessionMode, cookies: List<Cookie>) {
        if (mode == SessionMode.NONE) return
        val target = this.cookies.getOrPut(mode) { mutableListOf() }
        cookies.forEach { cookie ->
            target.removeAll { it.name == cookie.name && it.domain == cookie.domain && it.path == cookie.path }
            if (cookie.expiresAt > System.currentTimeMillis()) target += cookie
        }
    }

    override fun clear(mode: SessionMode) {
        headers.remove(mode)
        cookies.remove(mode)
    }

    override fun clearAll() {
        headers.clear()
        cookies.clear()
    }

    override fun summary(url: HttpUrl, mode: SessionMode): SessionSummary {
        val items = cookies[mode].orEmpty().filter { it.expiresAt > System.currentTimeMillis() }
        return SessionSummary(
            mode = mode,
            headerCount = headers(url, mode).size,
            cookieCount = items.size,
            cookies = items.map {
                SessionCookieInfo(it.name, it.domain, it.path, it.secure, it.persistent)
            },
        )
    }

    private fun originKey(url: HttpUrl): String = "${url.scheme.lowercase()}://${url.host.lowercase()}:${url.port}"

    private fun defaultCookiePath(encodedPath: String): String {
        if (!encodedPath.startsWith('/') || encodedPath == "/") return "/"
        val lastSlash = encodedPath.lastIndexOf('/')
        return if (lastSlash <= 0) "/" else encodedPath.substring(0, lastSlash)
    }
}
