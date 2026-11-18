package com.imr.reqestra.presentation.plugins

import com.imr.reqestra.presentation.i18n.tr

import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imr.reqestra.core.plugins.PluginCatalog

@Composable
fun PluginsScreen(onOpen: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            StudioScreenHeader(
                title = tr("Extensiones"),
                subtitle = tr("Módulos integrados y capacidades previstas; sin código remoto."),
                eyebrow = tr("ECOSISTEMA"),
            )
        }
        items(PluginCatalog.extensions, key = { it.id }) { extension ->
            StudioSurfaceCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(extension.title, style = MaterialTheme.typography.titleMedium)
                    StudioTag(
                        tr(if (extension.available) "Integrado" else "Planeado"),
                        if (extension.available) StudioTone.SUCCESS else StudioTone.NEUTRAL,
                    )
                    Text(tr(extension.description))
                    Text("${tr("Capacidades")}: ${extension.capabilities.joinToString()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall)
                    if (extension.available) {
                        Button(onClick = { onOpen(extension.id) }) {
                            Text(tr("Abrir extensión"))
                        }
                    }
                }
            }
        }
    }
}
