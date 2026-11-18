package com.imr.reqestra.core.testing

import com.imr.reqestra.domain.model.ResponseSnapshot

data class AssertionResult(val label: String, val passed: Boolean, val detail: String)

data class AssertionConfig(
    val expectedStatus: Int? = 200,
    val maxDurationMs: Long? = 2_000,
    val requiredBodyText: String = "",
)

object ResponseAssertions {
    fun evaluate(response: ResponseSnapshot, config: AssertionConfig): List<AssertionResult> = buildList {
        config.expectedStatus?.let { expected ->
            add(AssertionResult(
                "Estado HTTP = $expected",
                response.statusCode == expected,
                "Recibido: ${response.statusCode}",
            ))
        }
        config.maxDurationMs?.let { maximum ->
            add(AssertionResult(
                "Duración ≤ $maximum ms",
                response.durationMs <= maximum,
                "Duración: ${response.durationMs} ms",
            ))
        }
        if (config.requiredBodyText.isNotEmpty()) {
            add(AssertionResult(
                "Contenido esperado",
                response.body.contains(config.requiredBodyText),
                if (response.bodyTruncated) "La respuesta fue truncada." else "Comprobación de texto.",
            ))
        }
    }
}
