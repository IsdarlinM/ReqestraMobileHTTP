package com.imr.reqestra.core.automation

import com.imr.reqestra.core.testing.AssertionConfig
import com.imr.reqestra.core.testing.AssertionResult
import com.imr.reqestra.core.testing.ResponseAssertions
import com.imr.reqestra.core.variables.VariableTemplate
import com.imr.reqestra.data.network.HttpEngine
import com.imr.reqestra.data.workspace.SavedRequest
import com.imr.reqestra.domain.model.OperationResult
import com.imr.reqestra.domain.model.RequestDraft
import com.imr.reqestra.domain.model.ResponseSnapshot
import kotlinx.coroutines.delay

data class RequestCheck(
    val name: String,
    val successful: Boolean,
    val statusCode: Int?,
    val durationMs: Long?,
    val assertions: List<AssertionResult>,
    val error: String?,
)
data class RunReport(val results: List<RequestCheck>) {
    val passed: Int get() = results.count { it.successful }
    val failed: Int get() = results.size - passed
}

/** Solo ejecuciones iniciadas por el usuario; sin carga masiva o llamadas paralelas. */
class CollectionRunner(private val httpEngine: HttpEngine) {
    suspend fun run(
        requests: List<SavedRequest>,
        variables: Map<String, String>,
        assertions: AssertionConfig,
        progress: (Int, Int) -> Unit,
    ): RunReport {
        require(requests.size in 1..20) { "Selecciona entre 1 y 20 peticiones." }
        val results = mutableListOf<RequestCheck>()
        requests.forEachIndexed { index, entry ->
            if (index > 0) delay(1000L)
            val resolved = runCatching { resolveDraft(entry.draft, variables) }
            val result = resolved.fold(
                onSuccess = { httpEngine.execute(it) },
                onFailure = { OperationResult.Failure(it.message ?: "Error al resolver las variables.") },
            )
            results += when (result) {
                is OperationResult.Success -> checkResult(entry.name, result.value, assertions)
                is OperationResult.Failure -> RequestCheck(
                    name = entry.name, successful = false, statusCode = null,
                    durationMs = null, assertions = emptyList(), error = result.message,
                )
            }
            progress(index + 1, requests.size)
        }
        return RunReport(results)
    }

    private fun resolveDraft(draft: RequestDraft, variables: Map<String, String>) = draft.copy(
        url = VariableTemplate.resolve(draft.url, variables),
        headersText = VariableTemplate.resolve(draft.headersText, variables),
        cookiesText = VariableTemplate.resolve(draft.cookiesText, variables),
        body = VariableTemplate.resolve(draft.body, variables),
    )

    private fun checkResult(name: String, response: ResponseSnapshot, config: AssertionConfig): RequestCheck {
        val checks = ResponseAssertions.evaluate(response, config)
        return RequestCheck(
            name = name, successful = checks.all { it.passed },
            statusCode = response.statusCode, durationMs = response.durationMs,
            assertions = checks, error = null,
        )
    }
}
