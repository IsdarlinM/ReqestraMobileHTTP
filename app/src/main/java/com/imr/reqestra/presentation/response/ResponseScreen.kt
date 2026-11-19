package com.imr.reqestra.presentation.response

import com.imr.reqestra.presentation.i18n.tr

import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone

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
import androidx.compose.ui.text.style.TextOverflow
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
        StudioScreenHeader(tr("Inspector HTTP"), tr("Examina la petición, los headers y la respuesta."), tr("DIAGNÓSTICO"))
        if (response == null) {
            StudioSurfaceCard {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Http, contentDescription = null)
                    Text(tr("Aún no hay respuesta"), style = MaterialTheme.typography.titleMedium)
                    Text(tr("Envía una petición para inspeccionar request y response."))
                }
            }
            return@Column
        }

        StudioSurfaceCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column {
                        Text(tr("Request enviado"), style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${response.request.method} ${response.request.url}",
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    OutlinedButton(onClick = {
                        val text = buildString {
                            append(response.request.method).append(' ').append(response.request.url).append('\n')
                            response.request.headers.forEach { append(it.name).append(": ").append(it.value).append('\n') }
                            if (response.request.bodyBytes > 0) append('\n').append(response.request.body)
                        }
                        clipboard.setText(AnnotatedString(text))
                    }, modifier = Modifier.align(Alignment.End)) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Text(tr(" Copiar"))
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

        StudioSurfaceCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        StudioTag(
                            "HTTP ${response.statusCode}",
                            when (response.statusCode) {
                                in 200..299 -> StudioTone.SUCCESS
                                in 300..399 -> StudioTone.WARNING
                                else -> StudioTone.ERROR
                            },
                        )
                        Text(if (response.statusMessage.isBlank()) tr("Respuesta del servidor") else response.statusMessage)
                    }
                    Icon(Icons.Outlined.Http, contentDescription = null)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudioTag(response.protocol, StudioTone.INFORMATION)
                    StudioTag("${response.durationMs} ms")
                    response.contentType?.let { StudioTag(it) }
                    if (response.receivedCookies > 0) StudioTag("${response.receivedCookies} cookie(s)", StudioTone.INFORMATION)
                }
            }
        }

        StudioSurfaceCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tr("Headers de respuesta"), style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = {
                        clipboard.setText(AnnotatedString(response.headers.joinToString("\n") { "${it.name}: ${it.value}" }))
                    }) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Text(tr(" Copiar"))
                    }
                }
                HorizontalDivider()
                HeaderList(response.headers)
            }
        }

        StudioSurfaceCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column {
                        Text(tr("Cuerpo de respuesta"), style = MaterialTheme.typography.titleMedium)
                        if (response.bodyTruncated) Text(tr("Contenido truncado a 4 MiB."), color = MaterialTheme.colorScheme.error)
                    }
                    OutlinedButton(
                        onClick = { clipboard.setText(AnnotatedString(response.body)) },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Text(tr(" Copiar"))
                    }
                }
                HorizontalDivider()
                Text(
                    if (response.body.isBlank()) tr("(respuesta sin cuerpo)") else response.body,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                )
            }
        }
    }
}

@Composable
private fun HeaderList(headers: List<ParsedHeader>) {
    if (headers.isEmpty()) {
        Text(tr("Sin headers."))
    } else {
        headers.forEach { header ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(header.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
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
