package com.imr.reqestra.core.variables

import com.imr.reqestra.domain.model.RequestDraft
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestParameterizerTest {
    @Test fun expandsQueryHeadersAndBody() {
        val draft = RequestDraft(
            url = "{{base}}/users?existing=1",
            queryParamsText = "q={{search}}\npage=2",
            headersText = "Authorization: Bearer {{token}}",
            body = "{\"user\":\"{{search}}\"}",
        )
        val expanded = RequestParameterizer.resolve(draft, mapOf(
            "base" to "https://api.example.com",
            "search" to "hello world",
            "token" to "abc",
        ))
        assertEquals("https://api.example.com/users?existing=1&q=hello%20world&page=2", expanded.url)
        assertEquals("Authorization: Bearer abc", expanded.headersText)
        assertEquals("{\"user\":\"hello world\"}", expanded.body)
        assertEquals("", expanded.queryParamsText)
    }

    @Test fun duplicatesAndSpecialCharactersAreEncoded() {
        val draft = RequestDraft(url = "https://example.com/?tag=original",
            queryParamsText = "tag=a&b\nnote=x=y")
        val url = RequestParameterizer.resolve(draft, emptyMap()).url.toHttpUrl()
        assertEquals(listOf("original", "a&b"), url.queryParameterValues("tag"))
        assertEquals("x=y", url.queryParameter("note"))
    }

    @Test fun rejectsMalformedInputAndMissingVariables() {
        assertTrue(runCatching {
            RequestParameterizer.resolve(RequestDraft(queryParamsText = "not-a-pair"), emptyMap())
        }.isFailure)
        assertTrue(runCatching {
            RequestParameterizer.resolve(RequestDraft(queryParamsText = "x={{missing}}"), emptyMap())
        }.isFailure)
        assertTrue(runCatching {
            RequestParameterizer.parseQuery("invalid name=x")
        }.isFailure)
    }
}
