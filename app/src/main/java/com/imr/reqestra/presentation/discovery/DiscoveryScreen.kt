package com.imr.reqestra.presentation.discovery

import com.imr.reqestra.presentation.i18n.tr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imr.reqestra.core.discovery.DiscoveryPlanner
import com.imr.reqestra.core.discovery.DiscoveryReport
import com.imr.reqestra.core.discovery.PassiveRouteHints
import com.imr.reqestra.core.discovery.ProbeKind
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.ResponseSnapshot
import com.imr.reqestra.presentation.components.StatusBanner
import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone

@Composable
fun DiscoveryScreen(
    draftUrl: String,
    response: ResponseSnapshot?,
    report: DiscoveryReport?,
    progress: String?,
    isLoading: Boolean,
    message: String?,
    isErrorMessage: Boolean,
    onDismissMessage: () -> Unit,
    onRun: (String, String, Set<HttpMethodOption>, Boolean) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var routes by remember { mutableStateOf("") }
    var parameters by remember { mutableStateOf("") }
    var methods by remember { mutableStateOf(DiscoveryPlanner.safeMethods) }
    var allowModifying by remember { mutableStateOf(false) }
    var confirmOpen by remember { mutableStateOf(false) }
    var ownsScope by remember { mutableStateOf(false) }
    val hints = remember(response, draftUrl) {
        PassiveRouteHints.extract(response?.request?.url ?: draftUrl, response)
    }

    if (confirmOpen) {
        AlertDialog(
            onDismissRequest = { confirmOpen = false },
            title = { Text(tr("Confirmar exploración HTTP")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(tr("Se enviarán peticiones reales únicamente al mismo origen de la URL del Estudio, a razón de una por segundo y sin seguir redirecciones."))
                    if (methods.any { it in DiscoveryPlanner.modifyingMethods }) {
                        Text(tr("Advertencia: POST, PUT, PATCH y DELETE pueden modificar o eliminar datos del servidor."),
                            color = MaterialTheme.colorScheme.error)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = ownsScope, onCheckedChange = { ownsScope = it })
                        Text(tr("Confirmo que tengo autorización para probar este origen."),
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = ownsScope && methods.isNotEmpty() && !isLoading,
                    onClick = {
                        confirmOpen = false
                        ownsScope = false
                        onRun(routes, parameters, methods, allowModifying)
                    },
                ) { Text(tr("Ejecutar")) }
            },
            dismissButton = {
                TextButton(onClick = { confirmOpen = false; ownsScope = false }) { Text(tr("Cancelar")) }
            },
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (message != null) item { StatusBanner(message, isErrorMessage, onDismissMessage) }
        item {
            StudioScreenHeader(
                title = tr("Explorar API"),
                subtitle = tr("Revisa rutas, parámetros candidatos y métodos en un origen autorizado."),
                eyebrow = "DISCOVERY LAB",
            )
        }
        item {
            StudioSurfaceCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("Origen de la exploración"), style = MaterialTheme.typography.titleMedium)
                    Text(draftUrl, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary)
                    StudioTag(tr("Solo mismo origen · máximo 24 solicitudes"), StudioTone.INFORMATION)
                    Text(tr("Las variables de la petición y sus headers se resuelven con el entorno activo. Los datos no se envían a ningún tercero."),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            StudioSurfaceCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("Rutas candidatas"), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = routes, onValueChange = { routes = it.take(2400) },
                        label = { Text(tr("Rutas absolutas (una por línea)")) },
                        placeholder = { Text(tr("/api/v1\n/health")) },
                        minLines = 3, maxLines = 7, modifier = Modifier.fillMaxWidth(),
                    )
                    if (hints.isNotEmpty()) {
                        OutlinedButton(
                            enabled = !isLoading,
                            onClick = {
                                routes = (routes.lineSequence().filter { it.isNotBlank() }.toList() + hints)
                                    .distinct().take(8).joinToString("\n")
                            },
                        ) { Text("Agregar ${hints.size} rutas de la última respuesta") }
                    }
                    Text(tr("Las rutas de una respuesta son pistas; no se analizan automáticamente."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider()
                    Text(tr("Parámetros candidatos"), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = parameters, onValueChange = { parameters = it.take(1500) },
                        label = { Text(tr("Nombres de parámetros (uno por línea)")) },
                        placeholder = { Text(tr("page\ninclude\nfilter")) },
                        minLines = 3, maxLines = 7, modifier = Modifier.fillMaxWidth(),
                    )
                    Text(tr("Compara la respuesta normal con la que incluye cada parámetro. Las diferencias son indicios, no confirmación de un parámetro oculto."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            StudioSurfaceCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(tr("Métodos HTTP"), style = MaterialTheme.typography.titleMedium)
                    Text(tr("GET, HEAD y OPTIONS no deben modificar datos según su semántica HTTP, aunque los servidores pueden tener comportamientos incorrectos."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    for (method in listOf(HttpMethodOption.GET, HttpMethodOption.HEAD, HttpMethodOption.OPTIONS)) {
                        MethodOptionRow(method, method in methods, !isLoading) { selected ->
                            methods = if (selected) methods + method else methods - method
                        }
                    }
                    HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = allowModifying,
                            enabled = !isLoading,
                            onCheckedChange = {
                                allowModifying = it
                                if (!it) methods = methods - DiscoveryPlanner.modifyingMethods
                            },
                        )
                        Text(tr("Habilitar métodos que pueden modificar datos"),
                            style = MaterialTheme.typography.bodySmall)
                    }
                    if (allowModifying) {
                        for (method in listOf(
                            HttpMethodOption.POST, HttpMethodOption.PUT,
                            HttpMethodOption.PATCH, HttpMethodOption.DELETE,
                        )) {
                            MethodOptionRow(method, method in methods, !isLoading) { selected ->
                                methods = if (selected) methods + method else methods - method
                            }
                        }
                        StudioTag(tr("Riesgo de cambios en el servidor"), StudioTone.WARNING)
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && methods.isNotEmpty(),
                        onClick = { confirmOpen = true },
                    ) { Text(tr("Revisar y ejecutar exploración")) }
                    if (isLoading) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text("Progreso: ${progress ?: "iniciando"}")
                        OutlinedButton(onClick = onCancel) { Text(tr("Cancelar exploración")) }
                    }
                }
            }
        }
        report?.let { finished ->
            item {
                Text("Resultados · ${finished.successCount} respuestas / ${finished.findings.size} solicitudes",
                    style = MaterialTheme.typography.titleMedium)
            }
            items(finished.findings.size) { index ->
                val finding = finished.findings[index]
                StudioSurfaceCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${finding.method} · ${finding.label}", style = MaterialTheme.typography.titleMedium)
                        StudioTag(
                            finding.status?.let { "HTTP $it" } ?: "Sin respuesta",
                            when (finding.status) {
                                null -> StudioTone.ERROR
                                in 200..299 -> StudioTone.SUCCESS
                                in 300..399 -> StudioTone.WARNING
                                else -> StudioTone.NEUTRAL
                            },
                        )
                        Text("${finding.durationMs ?: "—"} ms · ${finding.bytes ?: "—"} bytes",
                            style = MaterialTheme.typography.bodySmall)
                        if (finding.kind == ProbeKind.PARAMETER && finding.changedFromBaseline) {
                            StudioTag(tr("Diferencia observada; revisar manualmente"), StudioTone.INFORMATION)
                        }
                        finding.allow?.let { Text("Allow: $it", style = MaterialTheme.typography.bodySmall) }
                        finding.error?.let { Text(it, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MethodOptionRow(
    method: HttpMethodOption,
    checked: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, enabled = enabled, onCheckedChange = onChange)
        Text(method.wireName, style = MaterialTheme.typography.bodyMedium)
    }
}
