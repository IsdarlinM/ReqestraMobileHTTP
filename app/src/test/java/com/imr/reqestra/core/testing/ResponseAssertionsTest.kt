package com.imr.reqestra.core.testing

import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.RequestSnapshot
import com.imr.reqestra.domain.model.ResponseSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponseAssertionsTest {
    private fun response(code: Int, time: Long) = ResponseSnapshot(
        request = RequestSnapshot("GET", "https://example.com", emptyList(), "", BodyMode.NONE, 0),
        statusCode = code, statusMessage = "OK", protocol = "h2",
        durationMs = time, headers = emptyList(), body = """{"ok":true}""",
        bodyTruncated = false, contentType = "application/json",
        receivedAtEpochMs = 0L, receivedCookies = 0,
    )

    @Test fun checksStatusLatencyAndContent() {
        val result = ResponseAssertions.evaluate(
            response(200, 80), AssertionConfig(200, 100, """"ok":true"""))
        assertEquals(3, result.size)
        assertTrue(result.all { it.passed })
    }

    @Test fun reportsFailuresWithoutThrowing() {
        val result = ResponseAssertions.evaluate(response(500, 1500), AssertionConfig(200, 200))
        assertEquals(2, result.count { !it.passed })
    }
}
