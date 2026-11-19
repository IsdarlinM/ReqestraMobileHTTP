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
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Check
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.semantics.Role
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
import com.imr.reqestra.presentation.about.AboutScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReqestraApp(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var openMenu by remember { mutableStateOf(false) }
    var openLanguageMenu by remember { mutableStateOf(false) }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val compactLayout = screenWidthDp < 600
    val iconOnlyBrand = screenWidthDp < 390
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.reqestra_brand),
                            contentDescription = "Reqestra logo",
                            modifier = Modifier.size(if (iconOnlyBrand) 28.dp else 32.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        )
                        if (!iconOnlyBrand) Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Reqestra",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (!compactLayout) {
                                Text(
                                    "HTTP STUDIO  •  3.5",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Keep Language, Donate and Theme as three distinct touch targets.
                    // Explicit spacing works in both compact and expanded layouts.
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box {
                            val currentName = ReqestraLanguages.supported
                                .firstOrNull { it.code == language }?.name ?: "English"
                            IconButton(
                                onClick = { openLanguageMenu = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                            ) {
                                Icon(
                                    Icons.Outlined.Language,
                                    contentDescription = "${tr("Idioma de la interfaz")}: $currentName",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                            DropdownMenu(
                                expanded = openLanguageMenu,
                                onDismissRequest = { openLanguageMenu = false },
                                modifier = Modifier
                                    .widthIn(min = 228.dp)
                                    .background(MaterialTheme.colorScheme.surface),
                                containerColor = MaterialTheme.colorScheme.surface,
                            ) {
                                Text(
                                    tr("Idioma de la interfaz"),
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                )
                                ReqestraLanguages.supported.forEach { choice ->
                                    val selectedLanguage = language == choice.code
                                    val foreground = if (selectedLanguage)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurface
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (selectedLanguage)
                                                    MaterialTheme.colorScheme.primaryContainer
                                                else Color.Transparent,
                                            ),
                                        text = {
                                            Text(
                                                choice.name,
                                                color = foreground,
                                                style = if (selectedLanguage)
                                                    MaterialTheme.typography.titleSmall
                                                else MaterialTheme.typography.bodyMedium,
                                            )
                                        },
                                        leadingIcon = {
                                            Text(
                                                choice.code.uppercase(java.util.Locale.ROOT),
                                                color = foreground,
                                                style = MaterialTheme.typography.labelSmall,
                                            )
                                        },
                                        trailingIcon = {
                                            if (selectedLanguage) {
                                                Icon(
                                                    Icons.Outlined.Check,
                                                    contentDescription = tr("Seleccionado"),
                                                    tint = foreground,
                                                )
                                            }
                                        },
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
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier.size(44.dp),
                        ) {
                            Icon(
                                imageVector = if (darkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                                contentDescription = if (darkTheme) tr("Modo claro") else tr("Modo oscuro"),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
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
                AppSection.ABOUT -> AboutScreen()
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
private fun RowScope.SectionItem(
    selected: Boolean,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    // No oversized ripple/pointer highlight: a subtle background responds to touch.
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val palette = MaterialTheme.colorScheme
    val touchBackground = if (pressed) palette.surfaceVariant.copy(alpha = 0.55f)
        else Color.Transparent

    Column(
        modifier = Modifier.weight(1f)
            .heightIn(min = 64.dp)
            .padding(horizontal = 2.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(touchBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics { this.selected = selected }
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (selected) palette.primaryContainer else Color.Transparent,
                    RoundedCornerShape(12.dp),
                )
                .padding(horizontal = if (LocalConfiguration.current.screenWidthDp < 360) 7.dp else 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides
                    if (selected) palette.onPrimaryContainer else palette.onSurfaceVariant,
            ) {
                icon()
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) palette.onSurface else palette.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
