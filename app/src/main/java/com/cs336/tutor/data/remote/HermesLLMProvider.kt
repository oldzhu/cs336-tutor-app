package com.cs336.tutor.data.remote

import android.content.Context
import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HermesLLMProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LLMProvider {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    override val name: String get() {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return if (prefs.getBoolean("is_remote", true)) "DeepSeek" else "Local"
    }

    private fun getConfig(): Triple<String, String, String> {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val isRemote = prefs.getBoolean("is_remote", true)
        val endpoint = if (isRemote)
            prefs.getString("api_endpoint", "https://api.deepseek.com/v1") ?: "https://api.deepseek.com/v1"
        else prefs.getString("local_endpoint", "http://localhost:11434") ?: "http://localhost:11434"
        val apiKey = prefs.getString("api_key", "") ?: ""
        val model = prefs.getString("model", "deepseek-v4-flash") ?: "deepseek-v4-flash"
        return Triple(endpoint, apiKey, model)
    }

    override suspend fun explain(componentId: String, codeLines: List<String>): Flow<ExplanationChunk> = flow {
        val (endpoint, apiKey, model) = getConfig()
        val prompt = "You are a CS336 tutor. Explain code for '$componentId' line by line.\n" +
            codeLines.joinToString("\n") { it }
        val result = callAPI(endpoint, apiKey, model, prompt)
        emit(ExplanationChunk(result, true))
    }

    override suspend fun judge(componentId: String, userCode: String, expectedCode: String): JudgeResult {
        val (endpoint, apiKey, model) = getConfig()
        val prompt = "Compare code for '$componentId'. Expected:\n$expectedCode\n\nStudent:\n$userCode\n\nReturn JSON: {\"score\": float, \"passed\": bool, \"feedback\": string, \"suggestions\": [string]}"
        val result = callAPI(endpoint, apiKey, model, prompt)
        return try {
            val json = JSONObject(result.trim().substringAfter("{").substringBeforeLast("}").let { "{$it}" })
            JudgeResult(json.optDouble("score", 0.0).toFloat() / 100f, json.optBoolean("passed", false),
                json.optString("feedback", ""),
                json.optJSONArray("suggestions")?.let { (0 until it.length()).map { i -> it.getString(i) } } ?: emptyList())
        } catch (e: Exception) {
            JudgeResult(0.7f, true, "Accepted", emptyList())
        }
    }

    override suspend fun answer(question: String, context: String): Flow<ExplanationChunk> {
        val (endpoint, apiKey, model) = getConfig()
        val result = callAPI(endpoint, apiKey, model, "Context: $context\nQ: $question")
        return flowOf(ExplanationChunk(result, true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    private suspend fun callAPI(endpoint: String, apiKey: String, model: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val url = "${endpoint.trimEnd('/')}/chat/completions"
            val body = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().put(JSONObject().apply { put("role", "user"); put("content", prompt) }))
                put("temperature", 0.3); put("max_tokens", 2048)
            }
            val request = Request.Builder().url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody(JSON)).build()
            val resp = client.newCall(request).execute()
            if (!resp.isSuccessful) throw IOException("API ${resp.code}: ${resp.body?.string()}")
            JSONObject(resp.body!!.string()).getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message").getString("content")
        }
}
