package com.imr.reqestra.presentation.response

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Http
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.imr.reqestra.domain.model.ParsedHeader
import com.imr.reqestra.domain.model.ResponseSnapshot
import com.imr.reqestra.presentation.components.StatusBanner

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResponseScreen(
    response: ResponseSnapshot?,
    message: String?,
    isErrorMessage: Boolean,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (message != null) StatusBanner(message, isErrorMessage, onDismissMessage)
        Text("Inspector", style = MaterialTheme.typography.headlineSmall)
        if (response == null) {
            Card {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Http, contentDescription = null)
                    Text("Aún no hay respuesta", style = MaterialTheme.typography.titleMedium)
                    Text("Envía una petición para inspeccionar request y response.")
                }
            }
            return@Column
        }

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Request enviado", style = MaterialTheme.typography.titleMedium)
                        Text("${response.request.method} ${response.request.url}", color = MaterialTheme.colorScheme.primary)
                    }
                    OutlinedButton(onClick = {
                        val text = buildString {
                            append(response.request.method).append(' ').append(response.request.url).append('\n')
                            response.request.headers.forEach { append(it.name).append(": ").append(it.value).append('\n') }
                            if (response.request.bodyBytes > 0) append('\n').append(response.request.body)
                        }
                        clipboard.setText(AnnotatedString(text))
                    }) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Text(" Copiar")
                    }
                }
                HorizontalDivider()
                HeaderList(response.request.headers)
                if (response.request.bodyBytes > 0) {
                    Text("Body (${response.request.bodyBytes} bytes)", style = MaterialTheme.typography.titleMedium)
                    Text(response.request.body, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("HTTP ${response.statusCode}", style = MaterialTheme.typography.headlineSmall, color = statusColor(response.statusCode))
                        Text(response.statusMessage.ifBlank { "Respuesta del servidor" })
                    }
                    Icon(Icons.Outlined.Http, contentDescription = null)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text(response.protocol) })
                    AssistChip(
                        onClick = {},
                        leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null) },
                        label = { Text("${response.durationMs} ms") },
                    )
                    response.contentType?.let { AssistChip(onClick = {}, label = { Text(it) }) }
                    if (response.receivedCookies > 0) AssistChip(onClick = {}, label = { Text("${response.receivedCookies} cookie(s)") })
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Headers de respuesta", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = {
                        clipboard.setText(AnnotatedString(response.headers.joinToString("\n") { "${it.name}: ${it.value}" }))
                    }) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Text(" Copiar")
                    }
                }
                HorizontalDivider()
                HeaderList(response.headers)
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Cuerpo de respuesta", style = MaterialTheme.typography.titleMedium)
                        if (response.bodyTruncated) Text("Contenido truncado a 4 MiB.", color = MaterialTheme.colorScheme.error)
                    }
                    OutlinedButton(onClick = { clipboard.setText(AnnotatedString(response.body)) }) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Text(" Copiar")
                    }
                }
                HorizontalDivider()
                Text(
                    response.body.ifBlank { "(respuesta sin cuerpo)" },
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                )
            }
        }
    }
}

@Composable
private fun HeaderList(headers: List<ParsedHeader>) {
    if (headers.isEmpty()) {
        Text("Sin headers.")
    } else {
        headers.forEach { header ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(header.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(header.value, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
            }
        }
    }
}

@Composable
private fun statusColor(code: Int) = when (code) {
    in 200..299 -> MaterialTheme.colorScheme.primary
    in 300..399 -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.error
}
