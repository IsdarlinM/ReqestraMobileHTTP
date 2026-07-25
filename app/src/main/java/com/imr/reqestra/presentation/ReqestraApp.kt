package com.imr.reqestra.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imr.reqestra.presentation.history.HistoryScreen
import com.imr.reqestra.presentation.preview.PreviewScreen
import com.imr.reqestra.presentation.response.ResponseScreen
import com.imr.reqestra.presentation.session.SessionScreen
import com.imr.reqestra.presentation.workspace.WorkspaceScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReqestraApp(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text("Reqestra")
                        Text("Mobile HTTP Workbench · imr :: v2.0.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { Icon(Icons.Outlined.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
        bottomBar = {
            NavigationBar {
                SectionItem(state.selectedSection == AppSection.REQUEST, "Request", { Icon(Icons.Outlined.Code, null) }) { viewModel.selectSection(AppSection.REQUEST) }
                SectionItem(state.selectedSection == AppSection.PREVIEW, "Vista", { Icon(Icons.Outlined.Preview, null) }) { viewModel.selectSection(AppSection.PREVIEW) }
                SectionItem(state.selectedSection == AppSection.RESPONSE, "Response", { Icon(Icons.Outlined.ReceiptLong, null) }) { viewModel.selectSection(AppSection.RESPONSE) }
                SectionItem(state.selectedSection == AppSection.SESSION, "Sesión", { Icon(Icons.Outlined.Cookie, null) }) { viewModel.selectSection(AppSection.SESSION) }
                SectionItem(state.selectedSection == AppSection.HISTORY, "Historial", { Icon(Icons.Outlined.History, null) }) { viewModel.selectSection(AppSection.HISTORY) }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state.selectedSection) {
                AppSection.REQUEST -> WorkspaceScreen(
                    draft = state.draft,
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
                    onDismissMessage = viewModel::clearMessage,
                    modifier = Modifier.fillMaxSize(),
                )
                AppSection.PREVIEW -> PreviewScreen(state.draft.body, state.draft.bodyMode, Modifier.fillMaxSize())
                AppSection.RESPONSE -> ResponseScreen(state.response, state.message, state.isErrorMessage, viewModel::clearMessage, Modifier.fillMaxSize())
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
                AppSection.HISTORY -> HistoryScreen(state.history, viewModel::reuseHistory, viewModel::clearHistory, Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun SectionItem(selected: Boolean, label: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    NavigationBarItem(selected = selected, onClick = onClick, icon = icon, label = { Text(label) })
}
