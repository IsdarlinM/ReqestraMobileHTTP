package com.imr.reqestra.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.imr.reqestra.presentation.i18n.tr

/**
 * One entry point from the bottom navigation.
 * Donate stays in the visible header rather than being duplicated here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoreToolsSheet(
    onDismiss: () -> Unit,
    onSelect: (AppSection) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        contentColor = colors.onBackground,
        scrimColor = colors.scrim.copy(alpha = 0.45f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(start = 18.dp, end = 18.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = tr("Más herramientas"),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.onBackground,
                )
                Text(
                    text = tr("Acceso rápido a herramientas y resultados."),
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = colors.outlineVariant.copy(alpha = 0.7f),
            )
            ToolShortcut(
                tr("Historial de peticiones"),
                tr("Método, URL redactada, estado y duración."),
                Icons.Outlined.History,
            ) { onSelect(AppSection.HISTORY) }
            ToolShortcut(
                tr("Inspector completo"),
                tr("Examina la petición, los headers y la respuesta."),
                Icons.Outlined.Description,
            ) { onSelect(AppSection.RESPONSE) }
            ToolShortcut(
                tr("Vista previa"),
                tr("Previsualización segura del contenido de la petición."),
                Icons.Outlined.Code,
            ) { onSelect(AppSection.PREVIEW) }
            ToolShortcut(
                tr("Sesiones"),
                tr("Cookies y headers bajo tu control, guardados por origen."),
                Icons.Outlined.Lock,
            ) { onSelect(AppSection.SESSION) }
            ToolShortcut(
                tr("Extensiones"),
                tr("Módulos integrados y capacidades previstas; sin código remoto."),
                Icons.Outlined.Extension,
            ) { onSelect(AppSection.PLUGINS) }
        }
    }
}

@Composable
private fun ToolShortcut(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val palette = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 70.dp)
            .clip(shape)
            .background(if (pressed) palette.surfaceVariant else palette.surface)
            .border(1.dp, palette.outlineVariant.copy(alpha = 0.55f), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(42.dp)
                .background(palette.primaryContainer, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = palette.onPrimaryContainer)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = palette.onSurface,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = palette.onSurfaceVariant,
                maxLines = 2,
            )
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = palette.onSurfaceVariant,
        )
    }
}
