package com.imr.reqestra.presentation.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone
import com.imr.reqestra.presentation.components.StudioMethodTag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.BodyPolicy
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.RequestDraft
import com.imr.reqestra.domain.model.ResponseSnapshot
import com.imr.reqestra.core.curl.CurlCodec
import com.imr.reqestra.presentation.components.CodeEditor
import com.imr.reqestra.presentation.components.StatusBanner

private enum class EditorTab(val label: String) {
    GENERAL("General"), PARAMS("Query"), HEADERS("Headers"), COOKIES("Cookies"), BODY("Body"), OPTIONS("Opciones")
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    draft: RequestDraft,
    response: ResponseSnapshot?,
    isLoading: Boolean,
    message: String?,
    isErrorMessage: Boolean,
    onUrlChange: (String) -> Unit,
    onQueryParamsChange: (String) -> Unit,
    onMethodChange: (HttpMethodOption) -> Unit,
    onCustomMethodChange: (String) -> Unit,
    onHeadersChange: (String) -> Unit,
    onCookiesChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onBodyModeChange: (BodyMode) -> Unit,
    onContentTypeChange: (String) -> Unit,
    onFollowRedirectsChange: (Boolean) -> Unit,
    onAllowCleartextChange: (Boolean) -> Unit,
    onForceHttp1Change: (Boolean) -> Unit,
    onTimeoutChange: (String) -> Unit,
    onLoadBody: () -> Unit,
    onSend: () -> Unit,
    onCancel: () -> Unit,
    onExportCurl: () -> Result<String>,
    onImportDraft: (RequestDraft) -> Unit,
    onOpenResponse: () -> Unit,
    onFormatJson: () -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = EditorTab.entries
    val clipboard = LocalClipboardManager.current
    var curlDialogOpen by remember { mutableStateOf(false) }
    var curlInput by remember { mutableStateOf("") }
    var curlNotice by remember { mutableStateOf<String?>(null) }
    if (curlDialogOpen) {
        AlertDialog(
            onDismissRequest = { curlDialogOpen = false },
            title = { Text("Importar comando cURL") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pega un comando cURL. No se ejecuta ningún shell ni se leen archivos locales.")
                    OutlinedTextField(
                        value = curlInput,
                        onValueChange = { curlInput = it },
                        label = { Text("Comando cURL") },
                        minLines = 4,
                        maxLines = 8,
                    )
                    curlNotice?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    CurlCodec.importCommand(curlInput)
                        .onSuccess { imported ->
                            onImportDraft(imported)
                            curlDialogOpen = false
                            curlNotice = null
                        }
                        .onFailure { error -> curlNotice = error.message ?: "Comando no válido." }
                }) { Text("Importar") }
            },
            dismissButton = {
                TextButton(onClick = { curlDialogOpen = false }) { Text("Cancelar") }
            },
        )
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (message != null) {
            StatusBanner(message, isErrorMessage, onDismissMessage)
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("REQUEST WORKBENCH", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Text("Nueva petición", style = MaterialTheme.typography.titleLarge)
                        Text("Construye y envía solicitudes HTTP.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StudioTag(draft.sessionMode.label, StudioTone.INFORMATION)
                }
                OutlinedTextField(
                    value = draft.url,
                    onValueChange = onUrlChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("URL completa") },
                    placeholder = { Text("https://api.example.com/path?query=value") },
                    singleLine = true,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MethodSelector(draft.method, onMethodChange)
                    if (draft.method == HttpMethodOption.CUSTOM) {
                        OutlinedTextField(
                            value = draft.customMethod,
                            onValueChange = onCustomMethodChange,
                            label = { Text("Método personalizado") },
                            singleLine = true,
                        )
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSend, enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    ) {
                        Icon(Icons.Outlined.Send, contentDescription = null)
                        Text(" Enviar petición")
                    }
                    OutlinedButton(onClick = onLoadBody, enabled = !isLoading) {
                        Icon(Icons.Outlined.CloudDownload, contentDescription = null)
                        Text(" Cargar respuesta")
                    }
                    OutlinedButton(onClick = {
                        onExportCurl()
                            .onSuccess {
                                clipboard.setText(AnnotatedString(it))
                                curlNotice = null
                            }
                            .onFailure { curlNotice = it.message ?: "No se pudo exportar." }
                    }, enabled = !isLoading) { Text("Copiar cURL redactado") }
                    OutlinedButton(onClick = {
                        curlInput = ""
                        curlNotice = null
                        curlDialogOpen = true
                    }, enabled = !isLoading) { Text("Importar cURL") }
                    if (curlNotice != null && !curlDialogOpen) {
                        Text(curlNotice.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                    if (isLoading) {
                        OutlinedButton(onClick = onCancel) {
                            Icon(Icons.Outlined.Cancel, contentDescription = null)
                            Text(" Cancelar")
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        ) {
            Column {
                ScrollableTabRow(
                    selectedTabIndex = tabIndex, edgePadding = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(tab.label) })
                    }
                }
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    when (tabs[tabIndex]) {
                        EditorTab.GENERAL -> GeneralTab(draft)
                        EditorTab.PARAMS -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Parámetros de consulta", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Una línea por parámetro: nombre=valor. Usa {{variable}} en los valores. Los parámetros de la URL original se conservan y los nuevos se codifican automáticamente.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            CodeEditor(
                                value = draft.queryParamsText,
                                onValueChange = onQueryParamsChange,
                                label = "Query params · nombre=valor",
                                minLines = 7,
                            )
                            Text(
                                "Los headers, las cookies, la URL y el cuerpo también admiten {{variables}} del entorno activo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        EditorTab.HEADERS -> CodeEditor(
                            value = draft.headersText,
                            onValueChange = onHeadersChange,
                            label = "Headers específicos",
                            minLines = 10,
                        )
                        EditorTab.COOKIES -> {
                            Text("Cookies manuales", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Una por línea o separadas por punto y coma. Sobrescriben cookies de la sesión con el mismo nombre.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            CodeEditor(
                                value = draft.cookiesText,
                                onValueChange = onCookiesChange,
                                label = "nombre=valor",
                                minLines = 8,
                            )
                        }
                        EditorTab.BODY -> BodyTab(
                            draft = draft,
                            onModeChange = onBodyModeChange,
                            onContentTypeChange = onContentTypeChange,
                            onBodyChange = onBodyChange,
                            onFormatJson = onFormatJson,
                        )
                        EditorTab.OPTIONS -> OptionsTab(
                            draft = draft,
                            onFollowRedirectsChange = onFollowRedirectsChange,
                            onAllowCleartextChange = onAllowCleartextChange,
                            onForceHttp1Change = onForceHttp1Change,
                            onTimeoutChange = onTimeoutChange,
                        )
                    }
                }
            }
        }

        if (response != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("ÚLTIMA RESPUESTA", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        StudioTag(
                            "HTTP ${response.statusCode} · ${response.durationMs} ms",
                            when (response.statusCode) {
                                in 200..299 -> StudioTone.SUCCESS
                                in 300..399 -> StudioTone.WARNING
                                else -> StudioTone.ERROR
                            },
                        )
                        OutlinedButton(onClick = onOpenResponse) { Text("Ver inspector") }
                    }
                    Text("Respuesta · ${response.protocol}", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(response.body.take(500).ifBlank { "(sin contenido)" },
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                        maxLines = 8)
                }
            }
        }
    }
}

@Composable
private fun GeneralTab(draft: RequestDraft) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Línea de petición", style = MaterialTheme.typography.titleMedium)
        StudioMethodTag(if (draft.method == HttpMethodOption.CUSTOM) draft.customMethod else draft.method.wireName)
        Text(
            "${if (draft.method == HttpMethodOption.CUSTOM) draft.customMethod else draft.method.wireName} ${draft.url}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        HorizontalDivider()
        Text("Partes editables", style = MaterialTheme.typography.titleMedium)
        Text("Método, esquema, host, puerto, path, query string, headers, Host, cookies, tipo de contenido, cuerpo y opciones de transporte.")
        Text(
            "Content-Length, Transfer-Encoding y otros headers de framing quedan bajo control de OkHttp para evitar requests incoherentes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BodyTab(
    draft: RequestDraft,
    onModeChange: (BodyMode) -> Unit,
    onContentTypeChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onFormatJson: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BodyModeSelector(draft.bodyMode, onModeChange)
        if (draft.bodyMode == BodyMode.JSON) {
            OutlinedButton(onClick = onFormatJson) { Text("Formatear JSON") }
        }
        if (draft.bodyMode != BodyMode.NONE) {
            OutlinedTextField(
                value = draft.contentType,
                onValueChange = onContentTypeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Content-Type") },
                singleLine = true,
            )
            CodeEditor(
                value = draft.body,
                onValueChange = onBodyChange,
                label = if (draft.bodyMode == BodyMode.BASE64_BINARY) "Cuerpo codificado en Base64" else "Cuerpo del request",
                minLines = 14,
            )
        } else {
            Text("Esta petición se enviará sin cuerpo.")
        }
    }
}

@Composable
private fun OptionsTab(
    draft: RequestDraft,
    onFollowRedirectsChange: (Boolean) -> Unit,
    onAllowCleartextChange: (Boolean) -> Unit,
    onForceHttp1Change: (Boolean) -> Unit,
    onTimeoutChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SwitchRow("Seguir redirecciones", "Conserva el comportamiento estándar de OkHttp.", draft.followRedirects, onFollowRedirectsChange)
        SwitchRow("Permitir HTTP", "Solo para laboratorios autorizados; HTTPS continúa siendo el valor seguro.", draft.allowCleartext, onAllowCleartextChange)
        SwitchRow("Forzar HTTP/1.1", "Útil para comparar comportamiento frente a negociación HTTP/2.", draft.forceHttp1, onForceHttp1Change)
        OutlinedTextField(
            value = draft.timeoutSeconds.takeIf { it > 0 }?.toString().orEmpty(),
            onValueChange = onTimeoutChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Timeout total (1-120 segundos)") },
            singleLine = true,
        )
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MethodSelector(selected: HttpMethodOption, onSelected: (HttpMethodOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.wireName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Método") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            HttpMethodOption.entries.forEach { method ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(method.wireName)
                            Text(
                                when (method.bodyPolicy) {
                                    BodyPolicy.FORBIDDEN -> "Sin body"
                                    BodyPolicy.REQUIRED -> "Body requerido"
                                    BodyPolicy.OPTIONAL -> "Body opcional"
                                },
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                    onClick = { onSelected(method); expanded = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BodyModeSelector(selected: BodyMode, onSelected: (BodyMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            label = { Text("Tipo de cuerpo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BodyMode.entries.forEach { mode ->
                DropdownMenuItem(text = { Text(mode.label) }, onClick = { onSelected(mode); expanded = false })
            }
        }
    }
}
