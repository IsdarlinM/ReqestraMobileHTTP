package com.imr.reqestra.core.discovery

import com.imr.reqestra.domain.model.HttpMethodOption
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryPlannerTest {
    @Test fun boundedPlanKeepsTheSameOrigin() {
        val plan = DiscoveryPlanner.plan(
            "https://api.example.com:8443/v1/accounts?state=active",
            "/health\n/api/v1",
            "page\ninclude",
            setOf(HttpMethodOption.GET, HttpMethodOption.HEAD, HttpMethodOption.OPTIONS),
            false,
        )
        assertEquals(7, plan.size) // GET baseline, two routes, two params, HEAD, OPTIONS
        val source = plan.first().url.toHttpUrl()
        assertTrue(plan.all { DiscoveryPlanner.sameOrigin(source, it.url.toHttpUrl()) })
        assertEquals(ProbeKind.BASELINE, plan.first().kind)
        assertTrue(plan.any { it.kind == ProbeKind.PARAMETER && it.label == "page" })
    }

    @Test fun modifyingMethodsRequireExplicitOptIn() {
        val blocked = runCatching {
            DiscoveryPlanner.plan("https://example.com/", "", "",
                setOf(HttpMethodOption.DELETE), false)
        }
        assertTrue(blocked.isFailure)
        val permitted = DiscoveryPlanner.plan("https://example.com/", "", "",
            setOf(HttpMethodOption.DELETE), true)
        assertEquals(2, permitted.size)
    }

    @Test fun pathTraversalOrExternalUrlsAreRejected() {
        for (path in listOf("/../admin", "https://elsewhere.example/", "//evil.example")) {
            assertTrue(runCatching {
                DiscoveryPlanner.plan("https://example.com/", path, "",
                    setOf(HttpMethodOption.GET), false)
            }.isFailure)
        }
    }

    @Test fun limitsCandidateCount() {
        val many = (1..9).joinToString("\n") { "/route$it" }
        assertTrue(runCatching {
            DiscoveryPlanner.plan("https://example.com", many, "",
                setOf(HttpMethodOption.GET), false)
        }.isFailure)
    }
}
