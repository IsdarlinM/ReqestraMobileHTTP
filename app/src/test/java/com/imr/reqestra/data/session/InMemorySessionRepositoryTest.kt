package com.imr.reqestra.data.session

import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.SessionMode
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemorySessionRepositoryTest {
    @Test fun sessionHeadersAreScopedByOrigin() {
        val repository = InMemorySessionRepository()
        val first = "https://first.example/api".toHttpUrl()
        val second = "https://second.example/api".toHttpUrl()

        repository.updateHeaders(
            first,
            SessionMode.MEMORY,
            listOf(ParsedHeader("Authorization", "Bearer first-origin")),
        )

        assertEquals("Bearer first-origin", repository.headers(first, SessionMode.MEMORY).single().value)
        assertTrue(repository.headers(second, SessionMode.MEMORY).isEmpty())
    }

    @Test fun sessionModeNoneDoesNotPersistHeaders() {
        val repository = InMemorySessionRepository()
        val url = "https://example.com/".toHttpUrl()

        repository.updateHeaders(url, SessionMode.NONE, listOf(ParsedHeader("X-Test", "value")))

        assertTrue(repository.headers(url, SessionMode.NONE).isEmpty())
    }
}
