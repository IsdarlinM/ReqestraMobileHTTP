package com.imr.reqestra.core.discovery

import com.imr.reqestra.core.variables.RequestParameterizer
import com.imr.reqestra.data.network.HttpEngine
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.OperationResult
import com.imr.reqestra.domain.model.RequestDraft
import com.imr.reqestra.domain.model.ResponseSnapshot
import com.imr.reqestra.domain.model.SessionMode
import kotlinx.coroutines.delay
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

enum class ProbeKind { BASELINE, ROUTE, PARAMETER, METHOD }

data class ProbeTarget(
    val kind: ProbeKind,
    val label: String,
    val method: HttpMethodOption,
    val url: String,
)
data class ProbeFinding(
    val kind: ProbeKind,
    val label: String,
    val method: String,
    val status: Int?,
    val durationMs: Long?,
    val bytes: Int?,
    val allow: String?,
    val changedFromBaseline: Boolean,
    val error: String?,
)
data class DiscoveryReport(val findings: List<ProbeFinding>) {
    val successCount: Int get() = findings.count { it.status != null }
}

object DiscoveryPlanner {
    private val candidateParam = Regex("[A-Za-z_][A-Za-z0-9_.-]{0,63}")
    private val pathPattern = Regex("/[A-Za-z0-9._~/-]{0,180}")
    val safeMethods: Set<HttpMethodOption> =
        setOf(HttpMethodOption.GET, HttpMethodOption.HEAD, HttpMethodOption.OPTIONS)
    val modifyingMethods: Set<HttpMethodOption> = setOf(
        HttpMethodOption.POST, HttpMethodOption.PUT,
        HttpMethodOption.PATCH, HttpMethodOption.DELETE,
    )

    fun sameOrigin(base: HttpUrl, next: HttpUrl) =
        base.scheme == next.scheme && base.host == next.host && base.port == next.port

    fun plan(
        baseUrl: String,
        routeLines: String,
        parameterLines: String,
        methods: Set<HttpMethodOption>,
        allowModifying: Boolean,
    ): List<ProbeTarget> {
        val base = baseUrl.toHttpUrlOrNull() ?: error("Introduce una URL de origen HTTP(S) válida.")
        require(base.username.isEmpty() && base.password.isEmpty()) { "La URL no debe contener credenciales." }
        require(baseUrl.length <= 8192) { "URL excesivamente larga." }
        require(routeLines.length <= 2400 && parameterLines.length <= 1500) {
            "Reduce la lista de candidatos."
        }
        require(methods.isNotEmpty()) { "Selecciona al menos un método." }
        require(methods.all { it in safeMethods || (allowModifying && it in modifyingMethods) }) {
            "Un método que puede modificar datos requiere autorización explícita."
        }
        val paths = routeLines.lineSequence().map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }.distinct().toList()
        val params = parameterLines.lineSequence().map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }.distinct().toList()
        require(paths.size <= 8 && params.size <= 8) { "Máximo ocho rutas y ocho parámetros por ejecución." }
        val targets = mutableListOf(ProbeTarget(ProbeKind.BASELINE, "Referencia", HttpMethodOption.GET, base.toString()))
        paths.forEach { path ->
            require(pathPattern.matches(path) && !path.contains("//") &&
                path.split('/').none { it == "." || it == ".." }) {
                "Ruta no permitida: usa rutas absolutas como /api/v1/usuarios."
            }
            val candidate = base.newBuilder().encodedPath(path).query(null).fragment(null).build()
            require(sameOrigin(base, candidate)) { "No se permiten cambios de origen." }
            targets += ProbeTarget(ProbeKind.ROUTE, path, HttpMethodOption.GET, candidate.toString())
        }
        params.forEach { name ->
            require(candidateParam.matches(name)) { "Parámetro candidato no válido: $name." }
            val candidate = base.newBuilder().addQueryParameter(name, "reqestra_probe").build()
            require(sameOrigin(base, candidate)) { "No se permiten cambios de origen." }
            targets += ProbeTarget(ProbeKind.PARAMETER, name, HttpMethodOption.GET, candidate.toString())
        }
        HttpMethodOption.entries.filter { it in methods }.forEach { method ->
            targets += ProbeTarget(ProbeKind.METHOD, method.wireName, method, base.toString())
        }
        val unique = targets.distinctBy { it.method.wireName + " " + it.url }
        require(unique.size in 1..24) { "La exploración excede 24 peticiones." }
        return unique
    }
}

/** Extrae rutas del contenido ya obtenido: no ejecuta solicitudes por sí solo. */
object PassiveRouteHints {
    private val jsonUrl = Regex(""""(?:href|url|path|endpoint)"\s*:\s*"([^"]{1,250})"""", RegexOption.IGNORE_CASE)
    private val htmlHref = Regex("""href\s*=\s*["']([^"']{1,250})["']""", RegexOption.IGNORE_CASE)
    private val headerLink = Regex("""<([^>]{1,250})>""")

    fun extract(baseUrl: String, response: ResponseSnapshot?): List<String> {
        val base = baseUrl.toHttpUrlOrNull() ?: return emptyList()
        if (response == null) return emptyList()
        val body = response.body.take(64_000)
        val raw = buildList {
            jsonUrl.findAll(body).forEach { add(it.groupValues[1].replace("\\/", "/")) }
            htmlHref.findAll(body).forEach { add(it.groupValues[1]) }
            response.headers.filter { it.name.equals("Link", true) }.forEach { header ->
                headerLink.findAll(header.value.take(2000)).forEach { add(it.groupValues[1]) }
            }
        }
        return raw.mapNotNull { link ->
            val resolved = base.resolve(link) ?: return@mapNotNull null
            if (!DiscoveryPlanner.sameOrigin(base, resolved)) return@mapNotNull null
            resolved.encodedPath.takeIf { it.isNotBlank() && it != "/" }
        }.distinct().take(8)
    }
}

/**
 * Se ejecuta solo después de la confirmación del usuario.
 * Una petición por segundo, sin redirecciones automáticas, mismo origen y sin cookies de sesión.
 * Los parámetros llamados "ocultos" son hipótesis, no hallazgos confirmados.
 */
class EndpointExplorer(private val httpEngine: HttpEngine) {
    suspend fun run(
        resolvedDraft: RequestDraft,
        targets: List<ProbeTarget>,
        progress: (Int, Int) -> Unit,
    ): DiscoveryReport {
        require(targets.size in 1..24) { "Plan demasiado grande." }
        val base = resolvedDraft.url.toHttpUrlOrNull() ?: error("URL no válida.")
        val findings = mutableListOf<ProbeFinding>()
        var baseline: Pair<Int, Int>? = null
        targets.forEachIndexed { index, target ->
            if (index > 0) delay(1000L)
            val requested = target.url.toHttpUrlOrNull()
            require(requested != null && DiscoveryPlanner.sameOrigin(base, requested)) {
                "El destino salió del origen autorizado."
            }
            val probe = resolvedDraft.copy(
                url = requested.toString(),
                method = target.method,
                bodyMode = BodyMode.NONE,
                body = "",
                contentType = "",
                queryParamsText = "",
                followRedirects = false,
                sessionMode = SessionMode.NONE,
                sessionHeadersText = "",
                timeoutSeconds = resolvedDraft.timeoutSeconds.coerceIn(1, 15),
            )
            val result = httpEngine.execute(probe)
            findings += when (result) {
                is OperationResult.Failure -> ProbeFinding(
                    target.kind, target.label, target.method.wireName,
                    null, null, null, null, false, result.message,
                )
                is OperationResult.Success -> {
                    val response = result.value
                    val size = response.body.toByteArray(Charsets.UTF_8).size
                    if (target.kind == ProbeKind.BASELINE) baseline = response.statusCode to size
                    ProbeFinding(
                        target.kind,
                        target.label,
                        target.method.wireName,
                        response.statusCode,
                        response.durationMs,
                        size,
                        response.headers.firstOrNull { it.name.equals("Allow", true) }?.value?.take(120),
                        target.kind == ProbeKind.PARAMETER &&
                            baseline?.let { it.first != response.statusCode || it.second != size } == true,
                        null,
                    )
                }
            }
            progress(index + 1, targets.size)
        }
        return DiscoveryReport(findings)
    }
}
