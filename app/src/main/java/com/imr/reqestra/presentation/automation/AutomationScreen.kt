package com.imr.reqestra.presentation.automation

import com.imr.reqestra.presentation.i18n.tr

import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imr.reqestra.presentation.components.StatusBanner
import com.imr.reqestra.core.automation.RunReport
import com.imr.reqestra.data.workspace.WorkspaceData

@Composable
fun AutomationScreen(
    workspace: WorkspaceData,
    report: RunReport?,
    progress: String?,
    isLoading: Boolean,
    onRun: (String, Int, Long) -> Unit,
    onCancel: () -> Unit,
    message: String?,
    isErrorMessage: Boolean,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val collections = workspace.requests.map { it.collection }.distinct().sorted()
    var chosen by remember(collections) { mutableStateOf(collections.firstOrNull().orEmpty()) }
    var menuOpen by remember { mutableStateOf(false) }
    var expected by remember { mutableStateOf("200") }
    var maximum by remember { mutableStateOf("2000") }
    val count = workspace.requests.count { it.collection == chosen }
    val parsedExpected = expected.toIntOrNull() ?: 0
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (message != null) item { StatusBanner(message, isErrorMessage, onDismissMessage) }
        item {
            StudioScreenHeader(
                title = tr("Automatización"),
                subtitle = tr("Pruebas secuenciales, medibles y controladas."),
                eyebrow = tr("TEST RUNNER"),
            )
            StudioTag(tr("Hasta 20 peticiones · 1 petición/segundo"), StudioTone.INFORMATION)
        }
        item {
            StudioSurfaceCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("Ejecutor de colecciones"), style = MaterialTheme.typography.titleMedium)
                    Box {
                        OutlinedButton(onClick = { menuOpen = true }, enabled = collections.isNotEmpty() && !isLoading) {
                            Text(if (chosen.isEmpty()) tr("Crear una colección primero") else "${tr("Colección")}: $chosen ▾")
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            collections.forEach { collection ->
                                DropdownMenuItem(text = { Text(collection) }, onClick = {
                                    chosen = collection
                                    menuOpen = false
                                })
                            }
                        }
                    }
                    Text("$count ${tr("Pruebas")} · ${tr("Entorno activo")}: ${workspace.activeEnvironment}")
                    OutlinedTextField(
                        value = expected, onValueChange = { expected = it.filter(Char::isDigit).take(3) },
                        label = { Text(tr("Código HTTP esperado")) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = maximum, onValueChange = { maximum = it.filter(Char::isDigit).take(6) },
                        label = { Text(tr("Latencia máxima (ms)")) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                    )
                    Text(tr("La ejecución envía las peticiones reales de la colección y podría modificar datos del servidor."),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && count in 1..20 && parsedExpected in 100..599 && (maximum.toLongOrNull() ?: 0) in 1..120000,
                        onClick = { onRun(chosen, expected.toInt(), maximum.toLong()) },
                    ) { Text(tr("Ejecutar pruebas ahora")) }
                    if (isLoading) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text("${tr("Progreso")}: ${progress ?: "..."}")
                        OutlinedButton(onClick = onCancel) { Text(tr("Cancelar ejecución")) }
                    }
                }
            }
        }
        report?.let { result ->
            item {
                Text("${tr("Resultados")} · ${result.passed} PASS / ${result.failed} FAIL",
                    style = MaterialTheme.typography.titleMedium)
            }
            items(result.results.size) { index ->
                val check = result.results[index]
                StudioSurfaceCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(check.name, style = MaterialTheme.typography.titleMedium)
                        StudioTag(if (check.successful) "PASS" else "FAIL",
                            if (check.successful) StudioTone.SUCCESS else StudioTone.ERROR)
                        Text("HTTP ${check.statusCode ?: "—"} · ${check.durationMs ?: "—"} ms")
                        check.assertions.forEach { assertion ->
                            Text("${if (assertion.passed) "✓" else "✗"} ${assertion.label}: ${assertion.detail}",
                                style = MaterialTheme.typography.bodySmall)
                        }
                        check.error?.let { Text(tr(it), color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }
}
