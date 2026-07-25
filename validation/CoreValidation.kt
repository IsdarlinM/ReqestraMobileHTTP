import com.imr.reqestra.core.request.RequestComposer
import com.imr.reqestra.core.security.SensitiveDataRedactor
import com.imr.reqestra.core.validation.RequestValidator
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.RequestDraft

private var passed = 0
private fun checkCase(name: String, block: () -> Boolean) {
    check(block()) { "FAILED: $name" }
    passed++
    println("[PASS] $name")
}

fun main() {
    checkCase("HTTPS accepted") { RequestValidator.validate(RequestDraft()).isSuccess }
    checkCase("HTTP blocked by default") { RequestValidator.validate(RequestDraft(url = "http://example.com")).isFailure }
    checkCase("HTTP allowed with lab opt-in") { RequestValidator.validate(RequestDraft(url = "http://example.com", allowCleartext = true)).isSuccess }
    checkCase("URL credentials rejected") { RequestValidator.validate(RequestDraft(url = "https://user:pass@example.com")).isFailure }
    checkCase("URL fragment rejected") { RequestValidator.validate(RequestDraft(url = "https://example.com/#x")).isFailure }
    checkCase("Host header editable") { RequestValidator.validate(RequestDraft(headersText = "Host: origin.example.com")).isSuccess }
    checkCase("Content-Length controlled") { RequestValidator.validate(RequestDraft(headersText = "Content-Length: 4")).isFailure }
    checkCase("CRLF header rejected") { runCatching { RequestValidator.parseHeaders("X-Test: ok\rInjected: yes") }.isFailure }
    checkCase("Custom method accepted") {
        RequestValidator.validate(RequestDraft(method = HttpMethodOption.CUSTOM, customMethod = "PROPFIND")).getOrThrow().methodName == "PROPFIND"
    }
    checkCase("Invalid custom method rejected") {
        RequestValidator.validate(RequestDraft(method = HttpMethodOption.CUSTOM, customMethod = "BAD METHOD")).isFailure
    }
    checkCase("GET body rejected") {
        RequestValidator.validate(RequestDraft(method = HttpMethodOption.GET, bodyMode = BodyMode.RAW, body = "x")).isFailure
    }
    checkCase("POST empty body generated") {
        RequestValidator.validate(RequestDraft(method = HttpMethodOption.POST, bodyMode = BodyMode.NONE)).getOrThrow().bodyBytes?.isEmpty() == true
    }
    checkCase("Base64 binary decoded") {
        RequestValidator.validate(RequestDraft(bodyMode = BodyMode.BASE64_BINARY, body = "aGVsbG8=")).getOrThrow().bodyBytes?.toString(Charsets.UTF_8) == "hello"
    }
    checkCase("Invalid Base64 rejected") {
        RequestValidator.validate(RequestDraft(bodyMode = BodyMode.BASE64_BINARY, body = "###")).isFailure
    }
    checkCase("Cookie parsing") {
        RequestValidator.parseCookies("sid=one; theme=dark\nsid=two") == mapOf("sid" to "two", "theme" to "dark")
    }
    checkCase("Cookie control characters rejected") {
        runCatching { RequestValidator.parseCookies("sid=ok\u0001bad") }.isFailure
    }
    checkCase("Request headers override session headers") {
        val result = RequestValidator.mergeHeaders(
            listOf(ParsedHeader("Authorization", "old"), ParsedHeader("Accept", "json")),
            listOf(ParsedHeader("authorization", "new")),
        )
        result.size == 2 && result.any { it.name == "authorization" && it.value == "new" } && result.none { it.value == "old" }
    }
    checkCase("Cookie precedence session < header < editor") {
        val composed = RequestComposer.compose(
            sessionHeaders = emptyList(),
            requestHeaders = listOf(ParsedHeader("Cookie", "sid=header; theme=dark")),
            sessionCookies = mapOf("sid" to "session", "locale" to "es"),
            manualCookies = mapOf("sid" to "editor"),
        )
        composed.cookies == mapOf("sid" to "editor", "locale" to "es", "theme" to "dark") &&
            composed.headers.single { it.name == "Cookie" }.value.contains("sid=editor")
    }
    checkCase("Sensitive header redaction") {
        SensitiveDataRedactor.redact(listOf(ParsedHeader("Authorization", "Bearer secret"))).single().value == "••••••••"
    }
    checkCase("Sensitive query redaction") {
        SensitiveDataRedactor.redactUrl("https://example.com/api?token=secret&safe=1") == "https://example.com/api?token=REDACTED&safe=1"
    }
    checkCase("Timeout validation") { RequestValidator.validate(RequestDraft(timeoutSeconds = 121)).isFailure }
    println("TOTAL_PASS=$passed")
}
