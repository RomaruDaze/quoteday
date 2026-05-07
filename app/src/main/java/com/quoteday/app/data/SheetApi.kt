package com.quoteday.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object SheetApi {

    const val WEB_APP_URL = "https://script.google.com/macros/s/AKfycbwzEsz1ST808v0ov1-4Wn_YgQzVSCVRlfqXPXJ9lce3FwYXlvynCOEiZGWqdCtNHEzu5A/exec"

    private val client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private val JSON_TYPE = "application/json".toMediaType()

    suspend fun fetchAll(): List<Quote> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(WEB_APP_URL).get().build()
            val response = OkHttpClient().newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()
            response.close()
            val array = JSONArray(body)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Quote(
                    id = obj.getInt("id"),
                    text = obj.getString("text"),
                    author = obj.optString("author", "")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun add(id: Int, text: String, author: String) {
        post(JSONObject().apply {
            put("action", "add")
            put("id", id)
            put("text", text)
            put("author", author)
        })
    }

    suspend fun update(id: Int, text: String, author: String) {
        post(JSONObject().apply {
            put("action", "update")
            put("id", id)
            put("text", text)
            put("author", author)
        })
    }

    suspend fun delete(id: Int) {
        post(JSONObject().apply {
            put("action", "delete")
            put("id", id)
        })
    }

    private suspend fun post(body: JSONObject) = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(WEB_APP_URL)
                .post(body.toString().toRequestBody(JSON_TYPE))
                .build()
            // GAS runs doPost on the first request and returns 302 — data is already written.
            // We don't need to follow the redirect.
            client.newCall(request).execute().close()
        } catch (_: Exception) {
            // fire-and-forget — local Room already updated
        }
    }
}
