package com.imr.reqestra.presentation.components

import com.imr.reqestra.presentation.i18n.tr

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun StatusBanner(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val foreground = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(16.dp))
            .border(1.dp, foreground.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (isError) Icons.Outlined.ErrorOutline else Icons.Outlined.Info,
            contentDescription = null,
            tint = foreground,
        )
        Text(
            text = message,
            color = foreground,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        IconButton(onClick = onDismiss) {
            Icon(Icons.Outlined.Close, contentDescription = tr("Cerrar aviso"), tint = foreground)
        }
    }
}

@Composable
fun CodeEditor(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 10,
) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = (minLines.coerceIn(4, 8) * 22).dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                color = scheme.onSurface,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.35f),
                unfocusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.25f),
                focusedBorderColor = scheme.primary,
                cursorColor = scheme.primary,
            ),
            minLines = minLines.coerceIn(4, 8),
            shape = RoundedCornerShape(14.dp),
            supportingText = {
                Text("${value.toByteArray(Charsets.UTF_8).size} bytes UTF-8")
            },
        )
    }
}

enum class StudioTone { NEUTRAL, SUCCESS, INFORMATION, WARNING, ERROR }

@Composable
fun StudioTag(text: String, tone: StudioTone = StudioTone.NEUTRAL, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (background, foreground) = when (tone) {
        StudioTone.NEUTRAL -> scheme.surfaceVariant to scheme.onSurfaceVariant
        StudioTone.SUCCESS -> scheme.primaryContainer to scheme.onPrimaryContainer
        StudioTone.INFORMATION -> scheme.secondaryContainer to scheme.onSecondaryContainer
        StudioTone.WARNING -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        StudioTone.ERROR -> scheme.errorContainer to scheme.onErrorContainer
    }
    Row(
        modifier = modifier.widthIn(max = 260.dp)
            .background(background, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).background(foreground, CircleShape))
        Text(text, style = MaterialTheme.typography.labelMedium, color = foreground,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun StudioMethodTag(method: String, modifier: Modifier = Modifier) {
    val tone = when (method.uppercase()) {
        "GET", "HEAD" -> StudioTone.INFORMATION
        "POST" -> StudioTone.SUCCESS
        "PUT", "PATCH" -> StudioTone.WARNING
        "DELETE" -> StudioTone.ERROR
        else -> StudioTone.NEUTRAL
    }
    StudioTag(method, tone, modifier)
}

@Composable
fun StudioScreenHeader(
    title: String,
    subtitle: String,
    eyebrow: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(eyebrow.uppercase(), style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Text(title, style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground)
        Text(subtitle, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun StudioSurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
    ) {
        content()
    }
}
