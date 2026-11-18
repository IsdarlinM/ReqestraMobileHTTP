package com.imr.reqestra.presentation.history

import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioMethodTag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.imr.reqestra.domain.model.HistoryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    entries: List<HistoryEntry>,
    onReuse: (HistoryEntry) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            StudioScreenHeader(
                title = "Historial local",
                subtitle = "Método, URL redactada, estado y duración.",
                eyebrow = "ACTIVIDAD",
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = onClear, enabled = entries.isNotEmpty()) {
                Icon(Icons.Outlined.DeleteSweep, contentDescription = null)
                Text(" Limpiar")
            }
        }
        if (entries.isEmpty()) {
            StudioSurfaceCard {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.History, contentDescription = null)
                    Text("No hay peticiones registradas", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(entries, key = HistoryEntry::id) { entry ->
                    StudioSurfaceCard(modifier = Modifier.fillMaxWidth().clickable { onReuse(entry) }) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    StudioMethodTag(entry.method)
                                    Text("HTTP ${entry.statusCode}")
                                    Text("${entry.durationMs} ms", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(entry.url, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(formatTimestamp(entry.timestampEpochMs), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Outlined.Replay, contentDescription = "Reutilizar")
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(epochMs: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(epochMs))
