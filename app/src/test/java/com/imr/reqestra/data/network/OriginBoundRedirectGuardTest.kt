package com.imr.reqestra.data.network

import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class OriginBoundRedirectGuardTest {
    @Test fun doesNotForwardCrossOriginRedirectOrSecretHeaders() {
        val origin = MockWebServer()
        val foreign = MockWebServer()
        origin.start()
        foreign.start()
        try {
            origin.enqueue(MockResponse().setResponseCode(307).addHeader("Location", foreign.url("/sink")))
            foreign.enqueue(MockResponse().setResponseCode(200).setBody("received"))
            val client = OkHttpClient.Builder()
                .followRedirects(true)
                .addNetworkInterceptor(OriginBoundRedirectGuard(origin.url("/")))
                .build()
            val request = Request.Builder().url(origin.url("/start"))
                .header("X-Api-Key", "should-not-leak").build()
            assertThrows(IOException::class.java) {
                client.newCall(request).execute().use { }
            }
            assertEquals(1, origin.requestCount)
            assertEquals(0, foreign.requestCount)
        } finally {
            origin.shutdown()
            foreign.shutdown()
        }
    }

    @Test fun allowsSameOriginRedirect() {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setResponseCode(302).addHeader("Location", "/next"))
            server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))
            val client = OkHttpClient.Builder()
                .followRedirects(true)
                .addNetworkInterceptor(OriginBoundRedirectGuard(server.url("/")))
                .build()
            client.newCall(Request.Builder().url(server.url("/start")).build())
                .execute().use { assertEquals(200, it.code) }
            assertEquals(2, server.requestCount)
        } finally {
            server.shutdown()
        }
    }
}
