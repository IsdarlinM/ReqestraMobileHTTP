package com.imr.reqestra.core.validation

import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.RequestDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestValidatorTest {
    @Test fun hostHeaderIsEditable() {
        val result = RequestValidator.validate(RequestDraft(headersText = "Host: origin.example.com"))
        assertTrue(result.isSuccess)
    }

    @Test fun framingHeaderIsRejected() {
        val result = RequestValidator.validate(RequestDraft(headersText = "Content-Length: 1"))
        assertTrue(result.isFailure)
    }

    @Test fun cleartextRequiresOptIn() {
        assertTrue(RequestValidator.validate(RequestDraft(url = "http://example.com")).isFailure)
        assertTrue(RequestValidator.validate(RequestDraft(url = "http://example.com", allowCleartext = true)).isSuccess)
    }

    @Test fun customMethodIsAccepted() {
        val result = RequestValidator.validate(RequestDraft(method = HttpMethodOption.CUSTOM, customMethod = "PROPFIND"))
        assertEquals("PROPFIND", result.getOrThrow().methodName)
    }

    @Test fun base64BodyIsDecoded() {
        val result = RequestValidator.validate(
            RequestDraft(method = HttpMethodOption.POST, bodyMode = BodyMode.BASE64_BINARY, body = "aGVsbG8=")
        ).getOrThrow()
        assertEquals("hello", result.bodyBytes!!.toString(Charsets.UTF_8))
    }

    @Test fun cookieEditorParsesLastValue() {
        val cookies = RequestValidator.parseCookies("sid=one; theme=dark\nsid=two")
        assertEquals("two", cookies["sid"])
    }
}
