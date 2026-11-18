package com.imr.reqestra.core.plugins

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PluginCatalogTest {
    @Test fun pluginIdsUniqueAndNoRuntimeDownloads() {
        val ids = PluginCatalog.extensions.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
        assertTrue(PluginCatalog.extensions.any { it.id == "openapi" && it.available })
        assertTrue(PluginCatalog.extensions.any { it.id == "ai" && !it.available })
    }
}
