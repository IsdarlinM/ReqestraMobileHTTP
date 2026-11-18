package com.imr.reqestra.core.variables

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VariableTemplateTest {
    @Test fun substitutesAndDoesNotEvaluateNestedValues() {
        assertEquals(
            "https://api.example.com/v1?token={{nested}}",
            VariableTemplate.resolve("{{baseUrl}}/v1?token={{token}}",
                mapOf("baseUrl" to "https://api.example.com", "token" to "{{nested}}")),
        )
    }

    @Test fun refusesUnresolvedOrInvalidVariables() {
        assertTrue(runCatching { VariableTemplate.resolve("{{missing}}", emptyMap()) }.isFailure)
        assertTrue(runCatching { VariableTemplate.resolve("{{bad-name}}", mapOf("bad-name" to "x")) }.isFailure)
    }
}
