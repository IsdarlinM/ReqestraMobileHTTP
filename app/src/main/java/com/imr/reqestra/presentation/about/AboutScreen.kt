package com.imr.reqestra.presentation.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Source
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imr.reqestra.BuildConfig
import com.imr.reqestra.R
import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone
import com.imr.reqestra.presentation.i18n.tr

private const val REPOSITORY_URL = "https://github.com/IsdarlinM/ReqestraMobileHTTP"
private const val DOCUMENTATION_URL =
    "https://github.com/IsdarlinM/ReqestraMobileHTTP/blob/main/docs/USER_GUIDE.md"
private const val ISSUES_URL =
    "https://github.com/IsdarlinM/ReqestraMobileHTTP/issues"

/** Read-only help: no network operations occur until the user taps an external link. */
@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val links = LocalUriHandler.current

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StudioScreenHeader(
            title = tr("Acerca de Reqestra"),
            subtitle = tr("Cliente HTTP para crear, probar e inspeccionar solicitudes de forma controlada."),
            eyebrow = tr("ACERCA DE"),
        )
        StudioSurfaceCard {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reqestra_brand),
                    contentDescription = null,
                    modifier = Modifier.size(66.dp),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text("Reqestra", style = MaterialTheme.typography.titleLarge)
                    StudioTag("${tr("Versión")} ${BuildConfig.VERSION_NAME}", StudioTone.INFORMATION)
                }
            }
        }
        AboutCard(
            title = tr("Guía rápida"),
            items = listOf(
                tr("1. Crea una solicitud") to tr("Abre Estudio, introduce una URL HTTP(S) y elige el método."),
                tr("2. Ajusta y envía") to tr("Configura parámetros, headers o cuerpo y pulsa Enviar."),
                tr("3. Inspecciona el resultado") to tr("Revisa el estado, el cuerpo y los headers en Inspector."),
            ),
        )
        AboutCard(
            title = tr("Herramientas principales"),
            items = listOf(
                tr("Colecciones: guarda solicitudes y entornos.") to null,
                tr("Pruebas: ejecuta verificaciones de estado y latencia.") to null,
                tr("Explorar: comprueba rutas autorizadas con límites y confirmación.") to null,
            ),
        )
        AboutCard(
            title = tr("Privacidad y seguridad"),
            items = listOf(
                tr("Las solicitudes se envían únicamente cuando tú las ejecutas.") to null,
                tr("Los datos de trabajo se guardan localmente según el modo de almacenamiento elegido.") to null,
                tr("Antes de explorar un servidor, confirma que tienes autorización.") to null,
            ),
        )
        StudioSurfaceCard {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(tr("Ayuda y recursos"), style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { links.openUri(DOCUMENTATION_URL) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Outlined.Description, contentDescription = null)
                    Text(tr("Documentación"), modifier = Modifier.padding(horizontal = 8.dp))
                    Icon(Icons.Outlined.OpenInNew, contentDescription = null)
                }
                OutlinedButton(
                    onClick = { links.openUri(ISSUES_URL) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Outlined.ReportProblem, contentDescription = null)
                    Text(tr("Reportar un problema"), modifier = Modifier.padding(horizontal = 8.dp))
                    Icon(Icons.Outlined.OpenInNew, contentDescription = null)
                }
                OutlinedButton(
                    onClick = { links.openUri(REPOSITORY_URL) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Outlined.Source, contentDescription = null)
                    Text(tr("Repositorio de código"), modifier = Modifier.padding(horizontal = 8.dp))
                    Icon(Icons.Outlined.OpenInNew, contentDescription = null)
                }
                Text(
                    tr("Los enlaces se abren en el navegador."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AboutCard(
    title: String,
    items: List<Pair<String, String?>>,
) {
    StudioSurfaceCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            for ((heading, detail) in items) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(heading, style = MaterialTheme.typography.bodyMedium)
                    if (detail != null) {
                        Text(
                            detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
