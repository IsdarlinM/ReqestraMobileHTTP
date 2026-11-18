package com.imr.reqestra.core.curl

import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.RequestDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurlCodecTest {
    @Test fun importsPostJsonAndQuoteEscapes() {
        val draft = CurlCodec.importCommand("curl -X POST -H 'Content-Type: application/json' -H 'X-Test: it'\\''s ok' --data-raw '{\"ok\":true}' https://example.com/api").getOrThrow()
        assertEquals(HttpMethodOption.POST, draft.method)
        assertEquals(BodyMode.JSON, draft.bodyMode)
        assertEquals("{\"ok\":true}", draft.body)
        assertEquals("X-Test: it's ok", draft.headersText)
    }

    @Test fun doesNotExecuteShellOrReadFiles() {
        assertTrue(CurlCodec.importCommand("curl --data @/etc/passwd https://example.com").isFailure)
        assertTrue(CurlCodec.importCommand("curl -k https://example.com").isFailure)
        assertTrue(CurlCodec.importCommand("curl --output /tmp/file https://example.com").isFailure)
    }

    @Test fun sanitizesShareableCurl() {
        val output = CurlCodec.exportSanitized(RequestDraft(
            url = "https://example.com/?token=secret&q=a%2Fb",
            method = HttpMethodOption.POST,
            headersText = "Authorization: Bearer secret\nX-Api-Key: key123",
            bodyMode = BodyMode.JSON,
            body = "{\"password\":\"secret-value\"}",
        )).getOrThrow()
        assertFalse(output.contains("key123"))
        assertFalse(output.contains("secret-value"))
        assertFalse(output.contains("token=secret"))
        assertTrue(output.contains("q=REDACTED"))
        assertTrue(output.contains("REDACTED BODY"))
    }

    @Test fun importHttpRequiresManualCleartextApproval() {
        val result = CurlCodec.importCommand("curl http://example.com").getOrThrow()
        assertFalse(result.allowCleartext)
    }
}
