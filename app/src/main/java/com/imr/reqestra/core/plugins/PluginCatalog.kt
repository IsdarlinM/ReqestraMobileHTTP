package com.imr.reqestra.core.plugins

data class ExtensionDescriptor(
    val id: String,
    val title: String,
    val description: String,
    val capabilities: Set<String>,
    val available: Boolean,
)

/**
 * Catálogo cerrado de extensiones integradas. No descarga ni ejecuta código remoto.
 * Un futuro SDK de plugins deberá validar firmas y permisos por capacidad.
 */
object PluginCatalog {
    val extensions: List<ExtensionDescriptor> = listOf(
        ExtensionDescriptor("collections", "Collection Manager", "Colecciones locales cifradas", setOf("workspace.read", "workspace.write"), true),
        ExtensionDescriptor("variables", "Environment Variables", "Variables por entorno y plantillas", setOf("workspace.read"), true),
        ExtensionDescriptor("assertions", "Test Assertions", "Comprobaciones de estado, latencia y contenido", setOf("response.read"), true),
        ExtensionDescriptor("curl", "cURL Toolkit", "Importar y compartir peticiones redactadas", setOf("request.read", "request.write"), true),
        ExtensionDescriptor("openapi", "OpenAPI Importer", "Importador JSON de contratos OpenAPI", setOf("workspace.write"), true),
        ExtensionDescriptor("graphql", "GraphQL Studio", "Explorador de esquemas", setOf("network"), false),
        ExtensionDescriptor("proxy", "Proxy Connector", "Proxy configurable por usuario", setOf("network"), false),
        ExtensionDescriptor("ai", "AI Assistant", "Análisis privado opt-in", setOf("response.read", "network"), false),
    )
}
