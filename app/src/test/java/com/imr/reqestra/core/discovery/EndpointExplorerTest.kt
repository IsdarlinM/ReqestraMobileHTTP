package com.imr.reqestra.core.discovery

import com.imr.reqestra.data.network.HttpEngine
import com.imr.reqestra.data.session.InMemorySessionRepository
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.RequestDraft
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EndpointExplorerTest {
    @Test fun runsBoundedMethodsWithoutRedirects() = runBlocking {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))
            server.enqueue(MockResponse().setResponseCode(405).addHeader("Allow", "GET, OPTIONS"))
            val draft = RequestDraft(url = server.url("/api").toString(), allowCleartext = true)
            val targets = DiscoveryPlanner.plan(
                draft.url, "", "",
                setOf(HttpMethodOption.GET, HttpMethodOption.HEAD), false,
            )
            val report = EndpointExplorer(HttpEngine(InMemorySessionRepository()))
                .run(draft, targets) { _, _ -> }
            assertEquals(2, report.findings.size)
            assertEquals(200, report.findings.first().status)
            assertEquals(405, report.findings.last().status)
            assertEquals("GET, OPTIONS", report.findings.last().allow)
            assertEquals("GET", server.takeRequest().method)
            assertEquals("HEAD", server.takeRequest().method)
        } finally {
            server.shutdown()
        }
    }

    @Test fun rejectsTargetsOutsideTheApprovedOrigin() = runBlocking {
        val draft = RequestDraft(url = "https://example.com")
        val targets = listOf(ProbeTarget(ProbeKind.ROUTE, "outside", HttpMethodOption.GET, "https://attacker.example/"))
        val result = runCatching {
            EndpointExplorer(HttpEngine(InMemorySessionRepository())).run(draft, targets) { _, _ -> }
        }
        assertTrue(result.isFailure)
    }
}
