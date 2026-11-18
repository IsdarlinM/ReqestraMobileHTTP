package com.imr.reqestra.presentation

import com.imr.reqestra.R
import com.imr.reqestra.presentation.i18n.LocalReqestraLanguage
import com.imr.reqestra.presentation.i18n.ReqestraLanguages
import com.imr.reqestra.presentation.i18n.tr

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imr.reqestra.presentation.discovery.DiscoveryScreen
import com.imr.reqestra.presentation.automation.AutomationScreen
import com.imr.reqestra.presentation.collections.CollectionsScreen
import com.imr.reqestra.presentation.history.HistoryScreen
import com.imr.reqestra.presentation.plugins.PluginsScreen
import com.imr.reqestra.presentation.preview.PreviewScreen
import com.imr.reqestra.presentation.response.ResponseScreen
import com.imr.reqestra.presentation.session.SessionScreen
import com.imr.reqestra.presentation.workspace.WorkspaceScreen
import com.imr.reqestra.presentation.support.SupportScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReqestraApp(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var openMenu by remember { mutableStateOf(false) }
    var openLanguageMenu by remember { mutableStateOf(false) }
    val compactLayout = LocalConfiguration.current.screenWidthDp < 420
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("reqestra_ui", android.content.Context.MODE_PRIVATE)
    }
    val deviceLanguage = ReqestraLanguages.normalize(
        context.resources.configuration.locales.get(0)?.language
    )
    var language by rememberSaveable {
        mutableStateOf(
            preferences.getString("language", null)?.let(ReqestraLanguages::normalize)
                ?: deviceLanguage
        )
    }

    CompositionLocalProvider(LocalReqestraLanguage provides language) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.reqestra_brand),
                            contentDescription = "Reqestra logo",
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)),
                        )
                        Column {
                            Text("Reqestra", style = MaterialTheme.typography.titleMedium, maxLines = 1)
                            if (!compactLayout) {
                                Text(
                                    "HTTP STUDIO  •  3.4",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { openLanguageMenu = true }) {
                            Icon(Icons.Outlined.Language, contentDescription = tr("Idioma"))
                        }
                        DropdownMenu(
                            expanded = openLanguageMenu,
                            onDismissRequest = { openLanguageMenu = false },
                        ) {
                            ReqestraLanguages.supported.forEach { choice ->
                                DropdownMenuItem(
                                    text = { Text(choice.name) },
                                    onClick = {
                                        language = choice.code
                                        preferences.edit().putString("language", choice.code).apply()
                                        openLanguageMenu = false
                                    },
                                )
                            }
                        }
                    }
                    if (compactLayout) {
                        IconButton(
                            onClick = { viewModel.selectSection(AppSection.SUPPORT) },
                            modifier = Modifier.size(44.dp).background(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                RoundedCornerShape(12.dp),
                            ),
                        ) {
                            Icon(
                                Icons.Outlined.FavoriteBorder,
                                contentDescription = tr("Donar"),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { viewModel.selectSection(AppSection.SUPPORT) },
                            modifier = Modifier.heightIn(min = 44.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            ),
                        ) {
                            Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(tr("Donar"), style = MaterialTheme.typography.labelLarge, maxLines = 1)
                        }
                    }
                    IconButton(onClick = { openMenu = true }) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = tr("Más herramientas"))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f))
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                SectionItem(state.selectedSection == AppSection.REQUEST, tr("Estudio"),
                    { Icon(Icons.Outlined.Code, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.REQUEST)
                }
                SectionItem(state.selectedSection == AppSection.COLLECTIONS, tr("Colecciones"),
                    { Icon(Icons.Outlined.Folder, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.COLLECTIONS)
                }
                SectionItem(state.selectedSection == AppSection.AUTOMATION, tr("Pruebas"),
                    { Icon(Icons.Outlined.PlayArrow, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.AUTOMATION)
                }
                SectionItem(state.selectedSection == AppSection.DISCOVERY, tr("Explorar"),
                    { Icon(Icons.Outlined.Search, contentDescription = null) }) {
                    viewModel.selectSection(AppSection.DISCOVERY)
                }
                SectionItem(
                    selected = state.selectedSection !in setOf(
                        AppSection.REQUEST, AppSection.COLLECTIONS,
                        AppSection.AUTOMATION, AppSection.DISCOVERY,
                    ),
                    label = tr("Más"),
                    icon = { Icon(Icons.Outlined.MoreHoriz, contentDescription = null) },
                    onClick = { openMenu = true },
                )
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
                    onQueryParamsChange = viewModel::updateQueryParams,
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
                    onExportCurl = viewModel::exportCurlSanitized,
                    onImportDraft = viewModel::importCurlDraft,
                    onOpenResponse = { viewModel.selectSection(AppSection.RESPONSE) },
                    onFormatJson = viewModel::formatJsonBody,
                    onDismissMessage = viewModel::clearMessage,
                    modifier = Modifier.fillMaxSize(),
                )
                AppSection.DISCOVERY -> DiscoveryScreen(
                    draftUrl = state.draft.url,
                    response = state.response,
                    report = state.discoveryReport,
                    progress = state.discoveryProgress,
                    isLoading = state.isLoading,
                    message = state.message,
                    isErrorMessage = state.isErrorMessage,
                    onDismissMessage = viewModel::clearMessage,
                    onRun = viewModel::runDiscovery,
                    onCancel = viewModel::cancelRequest,
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
                AppSection.PLUGINS -> PluginsScreen(
                    onOpen = { id ->
                        val destination = when (id) {
                            "collections", "variables", "openapi" -> AppSection.COLLECTIONS
                            "assertions" -> AppSection.AUTOMATION
                            "curl", "query" -> AppSection.REQUEST
                            "discovery" -> AppSection.DISCOVERY
                            else -> AppSection.PLUGINS
                        }
                        viewModel.selectSection(destination)
                    },
                )
                AppSection.SUPPORT -> SupportScreen()
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
    if (openMenu) {
        MoreToolsSheet(
            onDismiss = { openMenu = false },
            onSelect = { section ->
                openMenu = false
                viewModel.selectSection(section)
            },
        )
    }
    }
}

@Composable
private fun RowScope.SectionItem(selected: Boolean, label: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f).heightIn(min = 64.dp).semantics { this.selected = selected },
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 1.dp, vertical = 5.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier.background(
                    if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    RoundedCornerShape(13.dp),
                ).padding(horizontal = 15.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
