package com.imr.reqestra.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imr.reqestra.core.security.SensitiveDataRedactor
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

enum class AppSection { REQUEST, PREVIEW, RESPONSE, SESSION, HISTORY }

data class MainUiState(
    val draft: RequestDraft = RequestDraft(),
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
            sessionSummary = sessionRepository.summary(initialUrl, initialMode),
            sessionOrigin = originLabel(initialUrl),
        ),
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun updateUrl(value: String) = updateDraft { copy(url = value) }
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

    fun cancelRequest() = httpEngine.cancelActive()

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
            val result = withContext(Dispatchers.IO) { httpEngine.execute(draft) }
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
                            selectedSection = if (loadIntoBody) AppSection.REQUEST else AppSection.RESPONSE,
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
        httpEngine.cancelActive()
        super.onCleared()
    }
}
