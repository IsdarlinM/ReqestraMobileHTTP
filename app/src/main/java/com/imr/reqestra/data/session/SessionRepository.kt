package com.imr.reqestra.data.session

import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.SessionMode
import com.imr.reqestra.domain.model.SessionSummary
import okhttp3.Cookie
import okhttp3.HttpUrl

interface SessionRepository {
    fun preferredMode(): SessionMode
    fun setPreferredMode(mode: SessionMode)
    fun headers(url: HttpUrl, mode: SessionMode): List<ParsedHeader>
    fun updateHeaders(url: HttpUrl, mode: SessionMode, headers: List<ParsedHeader>)
    fun cookiesFor(url: HttpUrl, mode: SessionMode): List<Cookie>
    fun seedCookies(url: HttpUrl, mode: SessionMode, cookies: Map<String, String>)
    fun saveResponseCookies(mode: SessionMode, cookies: List<Cookie>)
    fun clear(mode: SessionMode)
    fun clearAll()
    fun summary(url: HttpUrl, mode: SessionMode): SessionSummary
}
