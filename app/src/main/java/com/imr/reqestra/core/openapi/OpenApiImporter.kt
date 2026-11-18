package com.imr.reqestra.core.openapi

import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.RequestDraft
import org.json.JSONObject

data class ImportedOperation(val name: String, val draft: RequestDraft)

/** Importador local de rutas OpenAPI JSON. No hace solicitudes al documento ni ejecuta $ref. */
object OpenApiImporter {
    private val methods = setOf("get", "post", "put", "patch", "delete", "options", "head")
    private const val MAX_DOCUMENT_CHARS = 256 * 1024

    fun importJson(document: String): List<ImportedOperation> {
        require(document.length <= MAX_DOCUMENT_CHARS) { "Documento OpenAPI demasiado grande (máx. 256 KiB)." }
        val root = JSONObject(document)
        require(root.has("openapi")) { "Este importador requiere un documento OpenAPI 3.x en JSON." }
        val servers = root.optJSONArray("servers")
        val base = servers?.optJSONObject(0)?.optString("url", "").orEmpty().trimEnd('/')
        require(base.startsWith("https://") || base.startsWith("http://")) {
            "La especificación debe definir servers[0].url con HTTP o HTTPS absoluto."
        }
        require(!base.contains('{') && !base.contains('}')) {
            "Resuelve las variables del servidor OpenAPI antes de importar."
        }
        val paths = root.optJSONObject("paths") ?: error("No existe paths en OpenAPI.")
        val output = mutableListOf<ImportedOperation>()
        val pathKeys = paths.keys()
        while (pathKeys.hasNext()) {
            val path = pathKeys.next()
            require(path.startsWith("/")) { "Ruta OpenAPI inválida." }
            val pathItem = paths.optJSONObject(path) ?: continue
            for (name in methods) {
                val operation = pathItem.optJSONObject(name) ?: continue
                val method = HttpMethodOption.entries.firstOrNull { it.wireName.equals(name, true) }
                    ?: continue
                val urlPath = Regex("""\{([A-Za-z][A-Za-z0-9_]*)\}""").replace(path) { match ->
                    "{{" + match.groupValues[1] + "}}"
                }
                val mode = if (method == HttpMethodOption.POST || method == HttpMethodOption.PUT ||
                    method == HttpMethodOption.PATCH) BodyMode.JSON else BodyMode.NONE
                output += ImportedOperation(
                    name = operation.optString("operationId").ifBlank {
                        "${name.uppercase()} $path"
                    }.take(80),
                    draft = RequestDraft(
                        url = base + urlPath,
                        method = method,
                        headersText = "Accept: application/json",
                        bodyMode = mode,
                        contentType = if (mode == BodyMode.JSON) "application/json" else "",
                        body = if (mode == BodyMode.JSON) "{}" else "",
                        allowCleartext = false,
                    ),
                )
                require(output.size <= 50) { "Máximo 50 operaciones por documento." }
            }
        }
        require(output.isNotEmpty()) { "No se encontraron operaciones HTTP compatibles." }
        return output
    }
}
