package com.imr.reqestra.core.discovery

import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.RequestSnapshot
import com.imr.reqestra.domain.model.ResponseSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PassiveRouteHintsTest {
    @Test fun extractsOnlySameOriginPaths() {
        val url = "https://api.example.com/v1"
        val response = ResponseSnapshot(
            request = RequestSnapshot("GET", url, emptyList(), "", BodyMode.NONE, 0),
            statusCode = 200, statusMessage = "OK", protocol = "h2",
            durationMs = 50, headers = listOf(
                ParsedHeader("Link", "</next>; rel=\"next\", <https://outside.example/x>; rel=\"alternate\"")
            ),
            body = """{"href": "/account", "url": "https://evil.example/admin"}""",
            bodyTruncated = false, contentType = "application/json",
            receivedAtEpochMs = 0L, receivedCookies = 0,
        )
        val hints = PassiveRouteHints.extract(url, response)
        assertTrue("/account" in hints)
        assertTrue("/next" in hints)
        assertEquals(2, hints.size)
    }
}
