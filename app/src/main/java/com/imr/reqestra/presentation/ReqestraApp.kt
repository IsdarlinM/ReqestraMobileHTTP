package com.imr.reqestra.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imr.reqestra.presentation.automation.AutomationScreen
import com.imr.reqestra.presentation.collections.CollectionsScreen
import com.imr.reqestra.presentation.history.HistoryScreen
import com.imr.reqestra.presentation.plugins.PluginsScreen
import com.imr.reqestra.presentation.preview.PreviewScreen
import com.imr.reqestra.presentation.response.ResponseScreen
import com.imr.reqestra.presentation.session.SessionScreen
import com.imr.reqestra.presentation.workspace.WorkspaceScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReqestraApp(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var openMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text("Reqestra", style = MaterialTheme.typography.titleMedium)
                        Text("HTTP STUDIO · v3.0", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { openMenu = true }) {
                            Icon(Icons.Outlined.MoreVert, contentDescription = "Más herramientas")
                        }
                        DropdownMenu(expanded = openMenu, onDismissRequest = { openMenu = false }) {
                            listOf(
                                AppSection.RESPONSE to "Inspector completo",
                                AppSection.PREVIEW to "Vista previa",
                                AppSection.SESSION to "Sesiones",
                                AppSection.PLUGINS to "Extensiones",
                            ).forEach { (section, label) ->
                                DropdownMenuItem(text = { Text(label) }, onClick = {
                                    openMenu = false
                                    viewModel.selectSection(section)
                                })
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            NavigationBar {
                SectionItem(state.selectedSection == AppSection.REQUEST, "Estudio",
                    { Icon(Icons.Outlined.Code, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.REQUEST)
                }
                SectionItem(state.selectedSection == AppSection.COLLECTIONS, "Colecciones",
                    { Icon(Icons.Outlined.Folder, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.COLLECTIONS)
                }
                SectionItem(state.selectedSection == AppSection.AUTOMATION, "Pruebas",
                    { Icon(Icons.Outlined.PlayArrow, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.AUTOMATION)
                }
                SectionItem(state.selectedSection == AppSection.HISTORY, "Historial",
                    { Icon(Icons.Outlined.History, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.HISTORY)
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state.selectedSection) {
                AppSection.REQUEST -> WorkspaceScreen(
                    draft = state.draft,
                    response = state.response,
                    isLoading = state.isLoading,
                    message = state.message,
                    isErrorMessage = state.isErrorMessage,
                    onUrlChange = viewModel::updateUrl,
                    onMethodChange = viewModel::updateMethod,
                    onCustomMethodChange = viewModel::updateCustomMethod,
                    onHeadersChange = viewModel::updateHeaders,
                    onCookiesChange = viewModel::updateCookies,
                    onBodyChange = viewModel::updateBody,
                    onBodyModeChange = viewModel::updateBodyMode,
                    onContentTypeChange = viewModel::updateContentType,
                    onFollowRedirectsChange = viewModel::updateFollowRedirects,
                    onAllowCleartextChange = viewModel::updateAllowCleartext,
                    onForceHttp1Change = viewModel::updateForceHttp1,
                    onTimeoutChange = viewModel::updateTimeout,
                    onLoadBody = viewModel::loadResponseBody,
                    onSend = viewModel::sendRequest,
                    onCancel = viewModel::cancelRequest,
                    onImportDraft = viewModel::importCurlDraft,
                    onOpenResponse = { viewModel.selectSection(AppSection.RESPONSE) },
                    onFormatJson = viewModel::formatJsonBody,
                    onDismissMessage = viewModel::clearMessage,
                    modifier = Modifier.fillMaxSize(),
                )
                AppSection.COLLECTIONS -> CollectionsScreen(
                    workspace = state.workspace,
                    onSave = viewModel::saveCurrentRequest,
                    onImportOpenApi = viewModel::importOpenApiDocument,
                    message = state.message,
                    isErrorMessage = state.isErrorMessage,
                    onDismissMessage = viewModel::clearMessage,
                    onLoad = viewModel::loadSavedRequest,
                    onDelete = viewModel::deleteSavedRequest,
                    onAddEnvironment = viewModel::addEnvironment,
                    onSelectEnvironment = viewModel::selectEnvironment,
                    onPutVariable = viewModel::setVariable,
                    onRemoveVariable = viewModel::removeVariable,
                )
                AppSection.AUTOMATION -> AutomationScreen(
                    workspace = state.workspace,
                    report = state.runReport,
                    progress = state.runProgress,
                    isLoading = state.isLoading,
                    onRun = viewModel::runCollection,
                    onCancel = viewModel::cancelRequest,
                    message = state.message,
                    isErrorMessage = state.isErrorMessage,
                    onDismissMessage = viewModel::clearMessage,
                )
                AppSection.PREVIEW -> PreviewScreen(state.draft.body, state.draft.bodyMode, Modifier.fillMaxSize())
                AppSection.RESPONSE -> ResponseScreen(state.response, state.message, state.isErrorMessage,
                    viewModel::clearMessage, Modifier.fillMaxSize())
                AppSection.PLUGINS -> PluginsScreen()
                AppSection.SESSION -> SessionScreen(
                    mode = state.draft.sessionMode,
                    origin = state.sessionOrigin,
                    headersText = state.draft.sessionHeadersText,
                    summary = state.sessionSummary,
                    message = state.message,
                    isErrorMessage = state.isErrorMessage,
                    onModeChange = viewModel::updateSessionMode,
                    onHeadersChange = viewModel::updateSessionHeaders,
                    onSave = viewModel::saveSessionSettings,
                    onClear = viewModel::clearSession,
                    onDismissMessage = viewModel::clearMessage,
                    modifier = Modifier.fillMaxSize(),
                )
                AppSection.HISTORY -> HistoryScreen(state.history, viewModel::reuseHistory,
                    viewModel::clearHistory, Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun SectionItem(selected: Boolean, label: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    NavigationBarItem(selected = selected, onClick = onClick, icon = icon, label = { Text(label) })
}
