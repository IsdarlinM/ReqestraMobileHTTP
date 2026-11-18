package com.imr.reqestra.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imr.reqestra.core.curl.CurlCodec
import com.imr.reqestra.core.security.SensitiveDataRedactor
import com.imr.reqestra.core.automation.CollectionRunner
import com.imr.reqestra.core.automation.RunReport
import com.imr.reqestra.core.testing.AssertionConfig
import com.imr.reqestra.core.variables.RequestParameterizer
import com.imr.reqestra.core.variables.VariableTemplate
import com.imr.reqestra.core.discovery.DiscoveryPlanner
import com.imr.reqestra.core.discovery.DiscoveryReport
import com.imr.reqestra.core.discovery.EndpointExplorer
import com.imr.reqestra.data.workspace.ApiEnvironment
import com.imr.reqestra.data.workspace.WorkspaceData
import com.imr.reqestra.data.workspace.WorkspaceStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import com.imr.reqestra.core.validation.RequestValidator
import com.imr.reqestra.data.history.HistoryStore
import com.imr.reqestra.data.network.HttpEngine
import com.imr.reqestra.data.session.AndroidSessionRepository
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HistoryEntry
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.OperationResult
import com.imr.reqestra.domain.model.RequestDraft
import com.imr.reqestra.domain.model.ResponseSnapshot
import com.imr.reqestra.domain.model.SessionMode
import com.imr.reqestra.domain.model.SessionSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

enum class AppSection { REQUEST, DISCOVERY, COLLECTIONS, AUTOMATION, HISTORY, PREVIEW, RESPONSE, SESSION, PLUGINS, SUPPORT }

data class MainUiState(
    val draft: RequestDraft = RequestDraft(),
    val workspace: WorkspaceData = WorkspaceData(),
    val runReport: RunReport? = null,
    val discoveryReport: DiscoveryReport? = null,
    val discoveryProgress: String? = null,
    val runProgress: String? = null,
    val response: ResponseSnapshot? = null,
    val history: List<HistoryEntry> = emptyList(),
    val sessionSummary: SessionSummary = SessionSummary(SessionMode.NONE, 0, 0, emptyList()),
    val sessionOrigin: String = "https://example.com:443",
    val selectedSection: AppSection = AppSection.REQUEST,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isErrorMessage: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionRepository = AndroidSessionRepository(application)
    private val httpEngine = HttpEngine(sessionRepository)
    private val historyStore = HistoryStore(application)
    private val workspaceStore = WorkspaceStore(application)
    private val collectionRunner = CollectionRunner(httpEngine)
    private val endpointExplorer = EndpointExplorer(httpEngine)
    private var discoveryJob: Job? = null
    private var automationJob: Job? = null
    private val initialMode = sessionRepository.preferredMode()
    private val initialUrl = RequestDraft().url.toHttpUrlOrNull()
        ?: error("La URL inicial de Reqestra no es válida.")
    private val initialHeaders = sessionRepository.headers(initialUrl, initialMode)

    private val _uiState = MutableStateFlow(
        MainUiState(
            draft = RequestDraft(
                sessionMode = initialMode,
                sessionHeadersText = initialHeaders.joinToString("\n") { "${it.name}: ${it.value}" },
            ),
            history = historyStore.load(),
            workspace = workspaceStore.load(),
            sessionSummary = sessionRepository.summary(initialUrl, initialMode),
            sessionOrigin = originLabel(initialUrl),
        ),
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun exportCurlSanitized(): Result<String> = runCatching {
        CurlCodec.exportSanitized(resolveDraft(_uiState.value.draft)).getOrThrow()
    }

    fun updateUrl(value: String) = updateDraft { copy(url = value) }
    fun updateQueryParams(value: String) = updateDraft { copy(queryParamsText = value) }
    fun importCurlDraft(imported: RequestDraft) {
        _uiState.update { current ->
            val target = imported.url.toHttpUrlOrNull()
            val mode = current.draft.sessionMode
            val targetHeaders = target?.let { sessionRepository.headers(it, mode) }.orEmpty()
            current.copy(
                draft = imported.copy(
                    sessionMode = mode,
                    sessionHeadersText = targetHeaders.joinToString("\n") { "${it.name}: ${it.value}" },
                ),
                sessionOrigin = target?.let(::originLabel) ?: "URL no válida",
                sessionSummary = target?.let { sessionRepository.summary(it, mode) }
                    ?: SessionSummary(mode, 0, 0, emptyList()),
                selectedSection = AppSection.REQUEST,
                message = "Comando cURL importado. Revisa la petición antes de enviarla.",
                isErrorMessage = false,
            )
        }
    }
    fun updateMethod(value: HttpMethodOption) = updateDraft {
        val mode = if (value.bodyPolicy == com.imr.reqestra.domain.model.BodyPolicy.FORBIDDEN) BodyMode.NONE else bodyMode
        copy(method = value, bodyMode = mode, contentType = if (mode == BodyMode.NONE) "" else contentType)
    }
    fun updateCustomMethod(value: String) = updateDraft { copy(customMethod = value) }
    fun updateHeaders(value: String) = updateDraft { copy(headersText = value) }
    fun updateCookies(value: String) = updateDraft { copy(cookiesText = value) }
    fun updateBody(value: String) = updateDraft { copy(body = value) }
    fun updateBodyMode(value: BodyMode) = updateDraft {
        copy(bodyMode = value, contentType = value.defaultContentType)
    }
    fun updateContentType(value: String) = updateDraft { copy(contentType = value) }
    fun updateFollowRedirects(value: Boolean) = updateDraft { copy(followRedirects = value) }
    fun updateAllowCleartext(value: Boolean) = updateDraft { copy(allowCleartext = value) }
    fun updateForceHttp1(value: Boolean) = updateDraft { copy(forceHttp1 = value) }
    fun updateTimeout(value: String) = updateDraft {
        copy(timeoutSeconds = value.filter(Char::isDigit).take(3).toIntOrNull() ?: 0)
    }
    fun updateSessionHeaders(value: String) = updateDraft { copy(sessionHeadersText = value) }

    fun updateSessionMode(value: SessionMode) {
        sessionRepository.setPreferredMode(value)
        val url = currentUrlOrNull()
        val existingHeaders = url?.let { sessionRepository.headers(it, value) }.orEmpty()
        _uiState.update {
            it.copy(
                draft = it.draft.copy(
                    sessionMode = value,
                    sessionHeadersText = existingHeaders.joinToString("\n") { h -> "${h.name}: ${h.value}" },
                ),
                sessionSummary = url?.let { target -> sessionRepository.summary(target, value) }
                    ?: SessionSummary(value, 0, 0, emptyList()),
                sessionOrigin = url?.let(::originLabel) ?: "URL no válida",
                message = "Modo de sesión: ${value.label}.",
                isErrorMessage = false,
            )
        }
    }

    fun saveSessionSettings() {
        val state = _uiState.value
        val url = currentUrlOrNull()
        if (url == null) {
            _uiState.update { it.copy(message = "La URL actual no permite determinar el origen de la sesión.", isErrorMessage = true) }
            return
        }
        val parsed = runCatching { RequestValidator.parseHeaders(state.draft.sessionHeadersText) }
        parsed.onFailure { error ->
            _uiState.update { it.copy(message = error.message, isErrorMessage = true) }
        }.onSuccess { headers ->
            sessionRepository.updateHeaders(url, state.draft.sessionMode, headers)
            refreshSession("Headers guardados para ${originLabel(url)}.")
        }
    }

    fun clearSession() {
        val mode = _uiState.value.draft.sessionMode
        sessionRepository.clear(mode)
        val url = currentUrlOrNull()
        _uiState.update {
            it.copy(
                draft = it.draft.copy(sessionHeadersText = ""),
                sessionSummary = url?.let { target -> sessionRepository.summary(target, mode) }
                    ?: SessionSummary(mode, 0, 0, emptyList()),
                sessionOrigin = url?.let(::originLabel) ?: "URL no válida",
                message = "Datos de la sesión ${mode.label.lowercase()} eliminados.",
                isErrorMessage = false,
            )
        }
    }

    fun selectSection(section: AppSection) {
        val url = currentUrlOrNull()
        _uiState.update { current ->
            if (section == AppSection.SESSION && url != null) {
                val headers = sessionRepository.headers(url, current.draft.sessionMode)
                current.copy(
                    selectedSection = section,
                    draft = current.draft.copy(
                        sessionHeadersText = headers.joinToString("\n") { "${it.name}: ${it.value}" },
                    ),
                    sessionSummary = sessionRepository.summary(url, current.draft.sessionMode),
                    sessionOrigin = originLabel(url),
                )
            } else {
                current.copy(
                    selectedSection = section,
                    sessionOrigin = url?.let(::originLabel) ?: "URL no válida",
                )
            }
        }
    }



    fun importOpenApiDocument(document: String) {
        runCatching {
            val imports = com.imr.reqestra.core.openapi.OpenApiImporter.importJson(document)
            val data = _uiState.value.workspace
            require(data.requests.size + imports.size <= 120) { "La importación supera el límite de 120 peticiones." }
            val next = data.copy(requests = data.requests + imports.map {
                workspaceStore.createRequest(it.name, "OpenAPI", it.draft)
            })
            workspaceStore.save(next)
            _uiState.update { it.copy(
                workspace = next,
                message = "Importadas ${imports.size} operaciones OpenAPI JSON.",
                isErrorMessage = false,
            ) }
        }.onFailure(::showWorkspaceError)
    }

    fun formatJsonBody() {
        runCatching {
            val draft = _uiState.value.draft
            require(draft.bodyMode == BodyMode.JSON) { "Selecciona el modo JSON." }
            require(draft.body.length <= 5 * 1024 * 1024) { "JSON demasiado grande." }
            val value = draft.body.trim()
            val pretty = if (value.startsWith("[")) org.json.JSONArray(value).toString(2)
                else org.json.JSONObject(value).toString(2)
            _uiState.update { it.copy(draft = it.draft.copy(body = pretty), message = "JSON formateado.", isErrorMessage = false) }
        }.onFailure(::showWorkspaceError)
    }

    fun saveCurrentRequest(name: String, collection: String) {
        runCatching {
            val current = _uiState.value
            val entry = workspaceStore.createRequest(name, collection, current.draft)
            val updated = current.workspace.copy(requests = current.workspace.requests + entry)
            workspaceStore.save(updated)
            _uiState.update { it.copy(workspace = updated, message = "Petición guardada en ${entry.collection}.", isErrorMessage = false) }
        }.onFailure { showWorkspaceError(it) }
    }

    fun loadSavedRequest(id: String) {
        val entry = _uiState.value.workspace.requests.find { it.id == id } ?: return
        val currentMode = _uiState.value.draft.sessionMode
        _uiState.update {
            it.copy(
                draft = entry.draft.copy(sessionMode = currentMode, sessionHeadersText = ""),
                selectedSection = AppSection.REQUEST,
                message = "Petición cargada desde ${entry.collection}.",
                isErrorMessage = false,
            )
        }
    }

    fun deleteSavedRequest(id: String) {
        mutateWorkspace { data -> data.copy(requests = data.requests.filterNot { it.id == id }) }
    }

    fun addEnvironment(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank() || trimmed.length > 40) {
            _uiState.update { it.copy(message = "Nombre de entorno inválido (1-40 caracteres).", isErrorMessage = true) }
            return
        }
        mutateWorkspace { data ->
            require(data.environments.none { it.name == trimmed }) { "El entorno ya existe." }
            data.copy(
                environments = data.environments + ApiEnvironment(trimmed),
                activeEnvironment = trimmed,
            )
        }
    }

    fun selectEnvironment(name: String) {
        mutateWorkspace { data ->
            require(data.environments.any { it.name == name }) { "Entorno desconocido." }
            data.copy(activeEnvironment = name)
        }
    }

    fun setVariable(name: String, value: String) {
        runCatching { VariableTemplate.requireName(name.trim()) }
            .onFailure { showWorkspaceError(it); return }
        if (value.length > 4096) {
            _uiState.update { it.copy(message = "Variable demasiado larga.", isErrorMessage = true) }
            return
        }
        mutateWorkspace { data ->
            data.copy(environments = data.environments.map { env ->
                if (env.name == data.activeEnvironment) env.copy(
                    variables = env.variables + (name.trim() to value),
                ) else env
            })
        }
    }

    fun removeVariable(name: String) {
        mutateWorkspace { data ->
            data.copy(environments = data.environments.map { env ->
                if (env.name == data.activeEnvironment) env.copy(
                    variables = env.variables - name,
                ) else env
            })
        }
    }

    fun runCollection(collection: String, expectedStatus: Int, maxDurationMs: Long) {
        if (_uiState.value.isLoading) return
        if (expectedStatus !in 100..599 || maxDurationMs < 1L || maxDurationMs > 120_000L) {
            _uiState.update { it.copy(message = "Assertion HTTP o tiempo inválidos.", isErrorMessage = true) }
            return
        }
        val data = _uiState.value.workspace
        val requests = data.requests.filter { it.collection == collection }
        if (requests.isEmpty() || requests.size > 20) {
            _uiState.update { it.copy(message = "La colección debe contener entre 1 y 20 peticiones.", isErrorMessage = true) }
            return
        }
        val variables = activeVariables()
        _uiState.update { it.copy(isLoading = true, runProgress = "0 / ${requests.size}", runReport = null, message = null) }
        automationJob = viewModelScope.launch {
            try {
                val report = withContext(Dispatchers.IO) {
                    collectionRunner.run(requests, variables, AssertionConfig(expectedStatus, maxDurationMs)) { done, total ->
                        _uiState.update { it.copy(runProgress = "$done / $total") }
                    }
                }
                _uiState.update { it.copy(runReport = report, message = "Pruebas completadas: ${report.passed} correctas, ${report.failed} fallidas.", isErrorMessage = report.failed > 0) }
            } catch (cancelled: CancellationException) {
                _uiState.update { it.copy(message = "Ejecución cancelada.", isErrorMessage = false) }
                throw cancelled
            } catch (error: Exception) {
                showWorkspaceError(error)
            } finally {
                _uiState.update { it.copy(isLoading = false, runProgress = null) }
            }
        }
    }

    fun runDiscovery(
        routes: String,
        parameters: String,
        methods: Set<HttpMethodOption>,
        allowModifying: Boolean,
    ) {
        if (_uiState.value.isLoading) return
        val prepared = runCatching {
            val request = resolveDraft(_uiState.value.draft)
            val targets = DiscoveryPlanner.plan(
                request.url,
                VariableTemplate.resolve(routes, activeVariables()),
                VariableTemplate.resolve(parameters, activeVariables()),
                methods,
                allowModifying,
            )
            val safe = request.copy(method = HttpMethodOption.GET,
                bodyMode = BodyMode.NONE, body = "", contentType = "",
                followRedirects = false)
            com.imr.reqestra.core.validation.RequestValidator.validate(safe).getOrThrow()
            request to targets
        }
        if (prepared.isFailure) {
            showWorkspaceError(prepared.exceptionOrNull() ?: IllegalArgumentException("Plan inválido"))
            return
        }
        val (request, targets) = prepared.getOrThrow()
        _uiState.update {
            it.copy(isLoading = true, discoveryReport = null,
                discoveryProgress = "0 / ${targets.size}", message = null)
        }
        discoveryJob = viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    endpointExplorer.run(request, targets) { current, total ->
                        _uiState.update { it.copy(discoveryProgress = "$current / $total") }
                    }
                }
                _uiState.update {
                    it.copy(
                        discoveryReport = result,
                        message = "Exploración terminada: ${result.successCount} respuestas de ${result.findings.size} intentos.",
                        isErrorMessage = false,
                    )
                }
            } catch (cancelled: CancellationException) {
                _uiState.update { it.copy(message = "Exploración cancelada.", isErrorMessage = false) }
                throw cancelled
            } catch (error: Exception) {
                showWorkspaceError(error)
            } finally {
                _uiState.update { it.copy(isLoading = false, discoveryProgress = null) }
            }
        }
    }

    private fun activeVariables(): Map<String, String> {
        val workspace = _uiState.value.workspace
        return workspace.environments.firstOrNull { it.name == workspace.activeEnvironment }?.variables.orEmpty()
    }

    private fun resolveDraft(draft: RequestDraft): RequestDraft =
        RequestParameterizer.resolve(draft, activeVariables())

    private fun showWorkspaceError(error: Throwable) {
        _uiState.update { it.copy(message = error.message ?: "Operación no disponible.", isErrorMessage = true) }
    }

    private fun mutateWorkspace(action: (WorkspaceData) -> WorkspaceData) {
        runCatching {
            val next = action(_uiState.value.workspace)
            workspaceStore.save(next)
            _uiState.update { it.copy(workspace = next, message = "Espacio de trabajo actualizado.", isErrorMessage = false) }
        }.onFailure(::showWorkspaceError)
    }

    fun loadResponseBody() {
        val source = _uiState.value.draft.copy(
            method = HttpMethodOption.GET,
            bodyMode = BodyMode.NONE,
            body = "",
            contentType = "",
        )
        runRequest(source, loadIntoBody = true)
    }

    fun sendRequest() = runRequest(_uiState.value.draft, loadIntoBody = false)

    fun cancelRequest() {
        discoveryJob?.cancel()
        automationJob?.cancel()
        httpEngine.cancelActive()
    }

    fun clearHistory() {
        historyStore.clear()
        _uiState.update { it.copy(history = emptyList(), message = "Historial eliminado.", isErrorMessage = false) }
    }

    fun reuseHistory(entry: HistoryEntry) {
        val option = HttpMethodOption.entries.firstOrNull { it.wireName == entry.method } ?: HttpMethodOption.CUSTOM
        _uiState.update {
            it.copy(
                draft = it.draft.copy(
                    url = entry.url,
                    method = option,
                    customMethod = if (option == HttpMethodOption.CUSTOM) entry.method else it.draft.customMethod,
                ),
                selectedSection = AppSection.REQUEST,
                message = "Se recuperaron URL y método. El historial no almacena headers, cookies ni cuerpos.",
                isErrorMessage = false,
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun runRequest(draft: RequestDraft, loadIntoBody: Boolean) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, message = null, isErrorMessage = false) }
        viewModelScope.launch {
            val prepared = runCatching { resolveDraft(draft) }
            val result = prepared.fold(
                onSuccess = { preparedDraft -> withContext(Dispatchers.IO) { httpEngine.execute(preparedDraft) } },
                onFailure = { OperationResult.Failure(it.message ?: "Variables no válidas.") },
            )
            when (result) {
                is OperationResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, message = result.message, isErrorMessage = true)
                }
                is OperationResult.Success -> {
                    val response = result.value
                    val history = addHistory(response)
                    val inferredMode = inferBodyMode(response.contentType)
                    _uiState.update { current ->
                        current.copy(
                            draft = if (loadIntoBody) {
                                current.draft.copy(
                                    body = response.body,
                                    bodyMode = inferredMode,
                                    contentType = response.contentType ?: inferredMode.defaultContentType,
                                )
                            } else current.draft,
                            response = response,
                            history = history,
                            sessionSummary = current.draft.url.toHttpUrlOrNull()?.let { target ->
                                sessionRepository.summary(target, current.draft.sessionMode)
                            } ?: current.sessionSummary,
                            sessionOrigin = current.draft.url.toHttpUrlOrNull()?.let(::originLabel)
                                ?: current.sessionOrigin,
                            selectedSection = AppSection.REQUEST,
                            isLoading = false,
                            message = if (loadIntoBody) "Respuesta cargada en el editor de cuerpo. ${result.notice.orEmpty()}".trim() else result.notice ?: "Petición completada.",
                            isErrorMessage = false,
                        )
                    }
                }
            }
        }
    }

    private fun addHistory(response: ResponseSnapshot): List<HistoryEntry> {
        val entry = HistoryEntry(
            id = System.nanoTime(),
            method = response.request.method,
            url = SensitiveDataRedactor.redactUrl(response.request.url),
            statusCode = response.statusCode,
            durationMs = response.durationMs,
            timestampEpochMs = response.receivedAtEpochMs,
        )
        val updated = (listOf(entry) + _uiState.value.history).take(HistoryStore.MAX_ENTRIES)
        historyStore.save(updated)
        return updated
    }

    private fun inferBodyMode(contentType: String?): BodyMode = when {
        contentType?.contains("json", true) == true -> BodyMode.JSON
        contentType?.contains("html", true) == true -> BodyMode.HTML
        contentType?.contains("xml", true) == true -> BodyMode.XML
        contentType?.contains("x-www-form-urlencoded", true) == true -> BodyMode.FORM_URLENCODED
        else -> BodyMode.RAW
    }

    private fun refreshSession(message: String) {
        val mode = _uiState.value.draft.sessionMode
        val url = currentUrlOrNull()
        _uiState.update {
            it.copy(
                sessionSummary = url?.let { target -> sessionRepository.summary(target, mode) }
                    ?: SessionSummary(mode, 0, 0, emptyList()),
                sessionOrigin = url?.let(::originLabel) ?: "URL no válida",
                message = message,
                isErrorMessage = false,
            )
        }
    }

    private fun currentUrlOrNull(): HttpUrl? = _uiState.value.draft.url.trim().toHttpUrlOrNull()

    private fun originLabel(url: HttpUrl): String = "${url.scheme}://${url.host}:${url.port}"

    private fun updateDraft(transform: RequestDraft.() -> RequestDraft) {
        _uiState.update { it.copy(draft = it.draft.transform(), message = null) }
    }

    override fun onCleared() {
        discoveryJob?.cancel()
        automationJob?.cancel()
        httpEngine.cancelActive()
        super.onCleared()
    }
}
