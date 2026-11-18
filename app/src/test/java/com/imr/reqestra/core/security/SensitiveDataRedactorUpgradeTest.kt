package com.imr.reqestra.core.security

import com.imr.reqestra.domain.model.ParsedHeader
import org.junit.Assert.assertEquals
import org.junit.Test

class SensitiveDataRedactorUpgradeTest {
    @Test fun preservesPercentEncodedQueryValues() {
        assertEquals(
            "https://example.com/path%2Fsegment?q=a%2Fb&token=REDACTED",
            SensitiveDataRedactor.redactUrl("https://example.com/path%2Fsegment?q=a%2Fb&token=abc"),
        )
    }

    @Test fun redactsEncodedSensitiveQueryKeys() {
        assertEquals(
            "https://example.com/?access%5Ftoken=REDACTED",
            SensitiveDataRedactor.redactUrl("https://example.com/?access%5Ftoken=abc"),
        )
    }

    @Test fun redactsAdditionalCommonSecretHeaders() {
        assertEquals("••••••••", SensitiveDataRedactor.redact(listOf(ParsedHeader("X-Amz-Security-Token", "key"))).single().value)
    }
}
