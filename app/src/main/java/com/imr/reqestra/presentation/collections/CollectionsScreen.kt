package com.imr.reqestra.presentation.collections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.imr.reqestra.presentation.components.StatusBanner
import com.imr.reqestra.data.workspace.SavedRequest
import com.imr.reqestra.data.workspace.WorkspaceData

@Composable
fun CollectionsScreen(
    workspace: WorkspaceData,
    onSave: (String, String) -> Unit,
    onImportOpenApi: (String) -> Unit,
    message: String?,
    isErrorMessage: Boolean,
    onDismissMessage: () -> Unit,
    onLoad: (String) -> Unit,
    onDelete: (String) -> Unit,
    onAddEnvironment: (String) -> Unit,
    onSelectEnvironment: (String) -> Unit,
    onPutVariable: (String, String) -> Unit,
    onRemoveVariable: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var requestName by remember { mutableStateOf("") }
    var openApiDialog by remember { mutableStateOf(false) }
    var openApiText by remember { mutableStateOf("") }
    var collectionName by remember { mutableStateOf("General") }
    var newEnvironment by remember { mutableStateOf("") }
    var variableName by remember { mutableStateOf("") }
    var variableValue by remember { mutableStateOf("") }
    var environmentMenu by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<SavedRequest?>(null) }
    val environment = workspace.environments.firstOrNull { it.name == workspace.activeEnvironment }

    if (openApiDialog) {
        AlertDialog(
            onDismissRequest = { openApiDialog = false },
            title = { Text("Importar OpenAPI JSON") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pega un documento OpenAPI 3.x en JSON con servers[0].url absoluto.")
                    OutlinedTextField(
                        value = openApiText, onValueChange = { openApiText = it.take(256 * 1024) },
                        label = { Text("Especificación JSON") },
                        minLines = 4, maxLines = 10, modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onImportOpenApi(openApiText)
                    openApiDialog = false
                    openApiText = ""
                }, enabled = openApiText.isNotBlank()) { Text("Importar") }
            },
            dismissButton = {
                TextButton(onClick = { openApiDialog = false }) { Text("Cancelar") }
            },
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Eliminar petición guardada") },
            text = { Text("¿Eliminar ${target.name} de ${target.collection}? No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(target.id)
                    deleteTarget = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") } },
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (message != null) item { StatusBanner(message, isErrorMessage, onDismissMessage) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Colecciones", style = MaterialTheme.typography.headlineSmall)
                Text("Organiza peticiones y entornos. Los datos se guardan cifrados en este dispositivo.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Guardar petición abierta", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = { openApiDialog = true }) { Text("Importar OpenAPI JSON") }
                    OutlinedTextField(
                        value = requestName, onValueChange = { requestName = it.take(80) },
                        label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    )
                    OutlinedTextField(
                        value = collectionName, onValueChange = { collectionName = it.take(60) },
                        label = { Text("Colección") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    )
                    Button(
                        onClick = { onSave(requestName, collectionName); requestName = "" },
                        enabled = requestName.isNotBlank() && collectionName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Guardar petición actual") }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Entornos y variables", style = MaterialTheme.typography.titleMedium)
                    Box {
                        OutlinedButton(onClick = { environmentMenu = true }) {
                            Text("Entorno: ${workspace.activeEnvironment} ▾")
                        }
                        DropdownMenu(expanded = environmentMenu, onDismissRequest = { environmentMenu = false }) {
                            workspace.environments.forEach { entry ->
                                DropdownMenuItem(
                                    text = { Text(entry.name) },
                                    onClick = { environmentMenu = false; onSelectEnvironment(entry.name) },
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = newEnvironment, onValueChange = { newEnvironment = it.take(40) },
                        label = { Text("Nuevo entorno") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    )
                    OutlinedButton(
                        onClick = { onAddEnvironment(newEnvironment); newEnvironment = "" },
                        enabled = newEnvironment.isNotBlank(),
                    ) { Text("Crear entorno") }
                    OutlinedTextField(
                        value = variableName, onValueChange = { variableName = it.take(64) },
                        label = { Text("Variable (por ejemplo, baseUrl)") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                    )
                    OutlinedTextField(
                        value = variableValue, onValueChange = { variableValue = it.take(4096) },
                        label = { Text("Valor privado") }, modifier = Modifier.fillMaxWidth(),
                        singleLine = true, visualTransformation = PasswordVisualTransformation(),
                    )
                    Button(onClick = {
                        onPutVariable(variableName, variableValue)
                        variableValue = ""
                    }, enabled = variableName.isNotBlank()) { Text("Guardar variable") }
                    environment?.variables?.keys?.sorted()?.forEach { key ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("{{${key}}}", modifier = Modifier.weight(1f))
                            IconButton(onClick = { onRemoveVariable(key) }) {
                                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar $key")
                            }
                        }
                    }
                    Text("Las variables se expanden antes de ejecutar la petición. No se muestran sus valores.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Text("Peticiones guardadas · ${workspace.requests.size}", style = MaterialTheme.typography.titleMedium)
        }
        if (workspace.requests.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.Folder, contentDescription = null)
                        Text("Todavía no hay peticiones guardadas.")
                    }
                }
            }
        }
        items(workspace.requests, key = { it.id }) { saved ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(saved.name, style = MaterialTheme.typography.titleMedium)
                        Text("${saved.collection} · ${saved.draft.method.wireName}",
                            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                        Text(saved.draft.url, maxLines = 1, overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { onLoad(saved.id) }) { Text("Abrir") }
                    IconButton(onClick = { deleteTarget = saved }) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar ${saved.name}")
                    }
                }
            }
        }
    }
}
