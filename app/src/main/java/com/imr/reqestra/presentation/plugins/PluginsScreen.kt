package com.imr.reqestra.presentation.plugins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imr.reqestra.core.plugins.PluginCatalog

@Composable
fun PluginsScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Extensiones", style = MaterialTheme.typography.headlineSmall)
            Text("Módulos integrados y capacidades previstas. No se ejecuta código descargado de terceros.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(PluginCatalog.extensions, key = { it.id }) { extension ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(extension.title, style = MaterialTheme.typography.titleMedium)
                    Text(if (extension.available) "Integrado" else "Planeado",
                        color = if (extension.available) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(extension.description)
                    Text("Capacidades: ${extension.capabilities.joinToString()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
