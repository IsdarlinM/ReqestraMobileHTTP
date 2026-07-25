package com.imr.reqestra.data.network

import com.imr.reqestra.data.session.InMemorySessionRepository
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.OperationResult
import com.imr.reqestra.domain.model.RequestDraft
import com.imr.reqestra.domain.model.SessionMode
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HttpEngineTest {
    private lateinit var server: MockWebServer
    private lateinit var engine: HttpEngine

    @Before fun setUp() {
        val held = HeldCertificate.Builder().addSubjectAlternativeName("localhost").build()
        val serverCertificates = HandshakeCertificates.Builder().heldCertificate(held).build()
        val clientCertificates = HandshakeCertificates.Builder().addTrustedCertificate(held.certificate).build()
        server = MockWebServer().apply { useHttps(serverCertificates.sslSocketFactory(), false); start() }
        val client = OkHttpClient.Builder()
            .sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager)
            .hostnameVerifier { _, _ -> true }
            .callTimeout(5, TimeUnit.SECONDS)
            .build()
        engine = HttpEngine(InMemorySessionRepository(), client)
    }

    @After fun tearDown() { server.shutdown() }

    @Test fun sendsEditedRequestAndKeepsCookieSession() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).addHeader("Set-Cookie", "sid=abc; Path=/; Secure").setBody("one"))
        server.enqueue(MockResponse().setResponseCode(200).setBody("two"))
        val draft = RequestDraft(
            url = server.url("/api?q=1").toString(),
            method = HttpMethodOption.POST,
            headersText = "X-Test: yes",
            bodyMode = BodyMode.JSON,
            contentType = "application/json",
            body = "{\"ok\":true}",
            sessionMode = SessionMode.MEMORY,
        )
        assertTrue(engine.execute(draft) is OperationResult.Success)
        assertTrue(engine.execute(draft.copy(method = HttpMethodOption.GET, bodyMode = BodyMode.NONE)) is OperationResult.Success)
        val first = server.takeRequest()
        val second = server.takeRequest()
        assertEquals("yes", first.getHeader("X-Test"))
        assertEquals("{\"ok\":true}", first.body.readUtf8())
        assertEquals("sid=abc", second.getHeader("Cookie"))
    }

    @Test fun carriesIntermediateRedirectCookieWhenSessionIsEnabled() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(302)
                .addHeader("Location", server.url("/next"))
                .addHeader("Set-Cookie", "flow=redirect; Path=/; Secure"),
        )
        server.enqueue(MockResponse().setResponseCode(200).setBody("done"))

        val result = engine.execute(
            RequestDraft(
                url = server.url("/start").toString(),
                method = HttpMethodOption.GET,
                bodyMode = BodyMode.NONE,
                body = "",
                contentType = "",
                sessionMode = SessionMode.MEMORY,
                followRedirects = true,
            ),
        )

        assertTrue(result is OperationResult.Success)
        server.takeRequest()
        val redirected = server.takeRequest()
        assertEquals("flow=redirect", redirected.getHeader("Cookie"))
    }

}
