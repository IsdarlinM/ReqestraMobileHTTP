package com.imr.reqestra.presentation.support

import com.imr.reqestra.presentation.i18n.tr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.imr.reqestra.BuildConfig
import com.imr.reqestra.core.donations.PayPalDonations
import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard
import com.imr.reqestra.presentation.components.StudioTag
import com.imr.reqestra.presentation.components.StudioTone

@Composable
fun SupportScreen(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val paypalUrl = PayPalDonations.hostedUrl(BuildConfig.PAYPAL_HOSTED_BUTTON_ID)
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StudioScreenHeader(
            title = tr("Apoya Reqestra"),
            eyebrow = "DONACIONES",
            subtitle = tr("Tu apoyo es opcional y no desbloquea funciones de la aplicación."),
        )
        StudioSurfaceCard {
            Column(modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StudioTag(tr("100 % voluntario"), StudioTone.INFORMATION)
                Text(tr("Invítanos a seguir mejorando Reqestra"),
                    style = MaterialTheme.typography.titleLarge)
                Text(tr("Si decides contribuir, el pago se realiza en una página segura de PayPal, fuera de Reqestra. No capturamos datos de tarjeta ni credenciales de PayPal."),
                    style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = { paypalUrl?.let(uriHandler::openUri) },
                    enabled = paypalUrl != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                    Text(tr(" Donar con PayPal"))
                }
                if (paypalUrl == null) {
                    Text(tr("El botón de donaciones aún no está configurado. El administrador debe establecer paypalHostedButtonId en Gradle y recompilar."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Text(tr("Las donaciones no se validan en la app, no afectan a tu acceso y no sustituyen pagos por bienes o servicios digitales."),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall)
    }
}
