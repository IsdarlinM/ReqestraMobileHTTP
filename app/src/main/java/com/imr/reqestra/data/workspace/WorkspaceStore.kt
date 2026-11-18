package com.imr.reqestra.data.workspace

import android.content.Context
import com.imr.reqestra.data.session.SecurePreferences
import com.imr.reqestra.domain.model.BodyMode
import com.imr.reqestra.domain.model.HttpMethodOption
import com.imr.reqestra.domain.model.RequestDraft
import com.imr.reqestra.domain.model.SessionMode
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

data class SavedRequest(
    val id: String,
    val collection: String,
    val name: String,
    val draft: RequestDraft,
)

data class ApiEnvironment(val name: String, val variables: Map<String, String> = emptyMap())

data class WorkspaceData(
    val requests: List<SavedRequest> = emptyList(),
    val environments: List<ApiEnvironment> = listOf(ApiEnvironment("Local")),
    val activeEnvironment: String = "Local",
)

/** Colecciones y variables cifradas en reposo; ningún secreto se exporta automáticamente. */
class WorkspaceStore(context: Context) {
    private val storage = SecurePreferences(context, "reqestra_workspace_v3", "reqestra_workspace_key_v3")

    fun load(): WorkspaceData = runCatching {
        val root = JSONObject(storage.getString("workspace") ?: return WorkspaceData())
        val requests = root.optJSONArray("requests") ?: JSONArray()
        val environments = root.optJSONArray("environments") ?: JSONArray()
        WorkspaceData(
            requests = buildList {
                for (i in 0 until minOf(requests.length(), 120)) {
                    val entry = requests.getJSONObject(i)
                    val value = entry.getJSONObject("draft")
                    add(
                        SavedRequest(
                            id = entry.getString("id"),
                            collection = entry.optString("collection", "General"),
                            name = entry.getString("name"),
                            draft = RequestDraft(
                                url = value.getString("url"),
                                queryParamsText = value.optString("queryParams", ""),
                                method = HttpMethodOption.valueOf(value.getString("method")),
                                customMethod = value.optString("customMethod", "PROPFIND"),
                                headersText = value.optString("headers", ""),
                                cookiesText = value.optString("cookies", ""),
                                bodyMode = BodyMode.valueOf(value.getString("bodyMode")),
                                contentType = value.optString("contentType", ""),
                                body = value.optString("body", ""),
                                followRedirects = value.optBoolean("redirects", false),
                                allowCleartext = value.optBoolean("cleartext", false),
                                forceHttp1 = value.optBoolean("http1", false),
                                timeoutSeconds = value.optInt("timeout", 30).coerceIn(1, 120),
                                sessionMode = SessionMode.NONE,
                            ),
                        ),
                    )
                }
            },
            environments = buildList {
                for (i in 0 until minOf(environments.length(), 12)) {
                    val item = environments.getJSONObject(i)
                    val variables = item.optJSONObject("variables") ?: JSONObject()
                    add(ApiEnvironment(item.getString("name"), buildMap {
                        val keys = variables.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            put(key, variables.getString(key))
                        }
                    }))
                }
            }.ifEmpty { listOf(ApiEnvironment("Local")) },
            activeEnvironment = root.optString("activeEnvironment", "Local"),
        ).let { data ->
            data.copy(activeEnvironment = data.activeEnvironment.takeIf { active ->
                data.environments.any { it.name == active }
            } ?: data.environments.first().name)
        }
    }.getOrElse { WorkspaceData() }

    fun save(data: WorkspaceData) {
        require(data.requests.size <= 120) { "Límite de 120 peticiones guardadas." }
        require(data.environments.size in 1..12) { "Se permiten de 1 a 12 entornos." }
        val root = JSONObject()
        root.put("activeEnvironment", data.activeEnvironment)
        root.put("requests", JSONArray().apply {
            data.requests.forEach { entry ->
                put(JSONObject().apply {
                    put("id", entry.id)
                    put("collection", entry.collection)
                    put("name", entry.name)
                    put("draft", JSONObject().apply {
                        val d = entry.draft
                        put("url", d.url)
                        put("queryParams", d.queryParamsText)
                        put("method", d.method.name)
                        put("customMethod", d.customMethod)
                        put("headers", d.headersText)
                        put("cookies", d.cookiesText)
                        put("bodyMode", d.bodyMode.name)
                        put("contentType", d.contentType)
                        put("body", d.body)
                        put("redirects", d.followRedirects)
                        put("cleartext", d.allowCleartext)
                        put("http1", d.forceHttp1)
                        put("timeout", d.timeoutSeconds)
                    })
                })
            }
        })
        root.put("environments", JSONArray().apply {
            data.environments.forEach { environment ->
                put(JSONObject().apply {
                    put("name", environment.name)
                    put("variables", JSONObject(environment.variables))
                })
            }
        })
        storage.putString("workspace", root.toString())
    }

    fun createRequest(name: String, collection: String, draft: RequestDraft): SavedRequest {
        require(name.trim().isNotEmpty() && name.length <= 80) { "Introduce un nombre de 1 a 80 caracteres." }
        require(collection.trim().isNotEmpty() && collection.length <= 60) { "La colección debe tener nombre." }
        return SavedRequest(UUID.randomUUID().toString(), collection.trim(), name.trim(), draft.copy(
            sessionMode = SessionMode.NONE, sessionHeadersText = "",
        ))
    }
}
