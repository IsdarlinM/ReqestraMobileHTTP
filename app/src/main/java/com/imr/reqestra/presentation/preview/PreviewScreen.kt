package com.imr.reqestra.presentation.preview

import com.imr.reqestra.presentation.i18n.tr

import com.imr.reqestra.presentation.components.StudioScreenHeader
import com.imr.reqestra.presentation.components.StudioSurfaceCard

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.imr.reqestra.domain.model.BodyMode

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PreviewScreen(
    body: String,
    bodyMode: BodyMode,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StudioScreenHeader(
            title = tr("Vista del cuerpo"),
            subtitle = tr("Previsualización segura del contenido de la petición."),
            eyebrow = "PREVIEW",
        )
        Text(
            if (bodyMode == BodyMode.HTML) tr("Renderizado HTML aislado, sin JavaScript ni acceso a red.") else "${tr("Vista del cuerpo")}: ${tr(bodyMode.label)}.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        StudioSurfaceCard(modifier = Modifier.fillMaxSize()) {
            if (bodyMode == BodyMode.HTML) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            setBackgroundColor(Color.TRANSPARENT)
                            settings.javaScriptEnabled = false
                            settings.domStorageEnabled = false
                            settings.databaseEnabled = false
                            settings.allowFileAccess = false
                            settings.allowContentAccess = false
                            settings.blockNetworkLoads = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = true
                            }
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL("about:blank", body, "text/html", "UTF-8", null)
                    },
                )
            } else {
                Text(
                    text = body.ifBlank { "(sin cuerpo)" },
                    modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                )
            }
        }
    }
}
