package com.imr.reqestra.presentation.session

import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imr.reqestra.domain.model.SessionMode
import com.imr.reqestra.domain.model.SessionSummary
import com.imr.reqestra.presentation.components.CodeEditor
import com.imr.reqestra.presentation.components.StatusBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    mode: SessionMode,
    origin: String,
    headersText: String,
    summary: SessionSummary,
    message: String?,
    isErrorMessage: Boolean,
    onModeChange: (SessionMode) -> Unit,
    onHeadersChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StudioScreenHeader(
                    title = "Gestor de sesión",
                    subtitle = "Cookies y headers bajo tu control, guardados por origen.",
                    eyebrow = "SEGURIDAD",
                )
                StudioTag("Origen: $origin", StudioTone.INFORMATION)
            }
        }
        if (message != null) item { StatusBanner(message, isErrorMessage, onDismissMessage) }
        item {
            StudioSurfaceCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SessionModeSelector(mode, onModeChange)
                    Text(
                        when (mode) {
                            SessionMode.NONE -> "No se cargan ni guardan cookies o headers de sesión."
                            SessionMode.MEMORY -> "La sesión vive hasta cerrar el proceso de la aplicación."
                            SessionMode.PERSISTENT -> "Cookies y headers se cifran con una clave AES-GCM protegida por Android Keystore y sobreviven reinicios."
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                    CodeEditor(
                        value = headersText,
                        onValueChange = onHeadersChange,
                        label = "Headers predeterminados de sesión",
                        minLines = 8,
                    )
                    Text(
                        "Los headers se guardan por origen; los específicos del request los sobrescriben. Authorization y otros secretos se cifran en modo persistente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onSave, enabled = mode != SessionMode.NONE) {
                            Icon(Icons.Outlined.Save, contentDescription = null)
                            Text(" Guardar")
                        }
                        OutlinedButton(onClick = onClear, enabled = mode != SessionMode.NONE) {
                            Icon(Icons.Outlined.DeleteForever, contentDescription = null)
                            Text(" Limpiar sesión")
                        }
                    }
                }
            }
        }
        item {
            StudioSurfaceCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Estado", style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Outlined.Cookie, contentDescription = null)
                    }
                    Text("${summary.headerCount} header(s) · ${summary.cookieCount} cookie(s)")
                }
            }
        }
        if (summary.cookies.isEmpty()) {
            item { Text("Aún no hay cookies en esta sesión.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(summary.cookies, key = { "${it.domain}|${it.path}|${it.name}" }) { cookie ->
                StudioSurfaceCard {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(cookie.name, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                        Text("${cookie.domain}${cookie.path}")
                        Text(
                            listOfNotNull(
                                "valor oculto",
                                if (cookie.secure) "Secure" else null,
                                if (cookie.persistent) "con expiración" else "cookie de sesión",
                            ).joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionModeSelector(selected: SessionMode, onSelected: (SessionMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            label = { Text("Persistencia") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SessionMode.entries.forEach { mode ->
                DropdownMenuItem(text = { Text(mode.label) }, onClick = { onSelected(mode); expanded = false })
            }
        }
    }
}
