package com.imr.reqestra.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.imr.reqestra.presentation.i18n.tr

/** Secondary navigation: all destinations are real, built-in app screens. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoreToolsSheet(
    onDismiss: () -> Unit,
    onSelect: (AppSection) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(tr("Más herramientas"), style = MaterialTheme.typography.titleLarge)
            Text(
                tr("Acceso rápido a herramientas y resultados."),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            ToolShortcut(
                tr("Apoyar proyecto · PayPal"),
                tr("Tu apoyo es opcional y no desbloquea funciones de la aplicación."),
                Icons.Outlined.FavoriteBorder,
            ) { onSelect(AppSection.SUPPORT) }
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
    ListItem(
        headlineContent = {
            Text(title, style = MaterialTheme.typography.titleSmall)
        },
        supportingContent = {
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        },
        leadingContent = {
            Icon(icon, contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth().heightIn(min = 68.dp).clickable(onClick = onClick),
    )
}
