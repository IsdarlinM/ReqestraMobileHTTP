package com.imr.reqestra.data.history

import android.content.Context
import com.imr.reqestra.domain.model.HistoryEntry
import org.json.JSONArray
import org.json.JSONObject

class HistoryStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun load(): List<HistoryEntry> = runCatching {
        val raw = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    HistoryEntry(
                        id = item.getLong("id"),
                        method = item.getString("method"),
                        url = item.getString("url"),
                        statusCode = item.getInt("statusCode"),
                        durationMs = item.getLong("durationMs"),
                        timestampEpochMs = item.getLong("timestampEpochMs"),
                    ),
                )
            }
        }
    }.getOrDefault(emptyList())

    fun save(entries: List<HistoryEntry>) {
        val array = JSONArray()
        entries.take(MAX_ENTRIES).forEach { entry ->
            array.put(
                JSONObject()
                    .put("id", entry.id)
                    .put("method", entry.method)
                    .put("url", entry.url)
                    .put("statusCode", entry.statusCode)
                    .put("durationMs", entry.durationMs)
                    .put("timestampEpochMs", entry.timestampEpochMs),
            )
        }
        preferences.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    fun clear() {
        preferences.edit().remove(KEY_HISTORY).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "reqestra_request_history"
        private const val KEY_HISTORY = "entries"
        const val MAX_ENTRIES = 50
    }
}
