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
class DeepSeekLLMProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LLMProvider {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val JSON = "application/json; charset=utf-8".toMediaType()

    override val name: String = "DeepSeek"

    private fun getConfig(): Triple<String, String, String> {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val endpoint = prefs.getString("api_endpoint", "https://api.deepseek.com/v1") ?: "https://api.deepseek.com/v1"
        val apiKey = prefs.getString("api_key", "") ?: ""
        val model = prefs.getString("model", "deepseek-v4-flash") ?: "deepseek-v4-flash"
        return Triple(endpoint, apiKey, model)
    }

    override suspend fun explain(componentId: String, codeLines: List<String>): Flow<ExplanationChunk> = flow {
        val (endpoint, apiKey, model) = getConfig()
        val prompt = "Explain each line of this $componentId code:\n" + codeLines.joinToString("\n")
        val result = chat(endpoint, apiKey, model, prompt)
        emit(ExplanationChunk(result, true))
    }

    override suspend fun judge(componentId: String, userCode: String, expectedCode: String): JudgeResult {
        val (endpoint, apiKey, model) = getConfig()
        val prompt = "Compare student code for $componentId.\nExpected:\n$expectedCode\n\nStudent:\n$userCode\n\nReply PASS or FAIL with short feedback."
        val result = chat(endpoint, apiKey, model, prompt)
        val passed = result.contains("PASS", ignoreCase = true) && !result.contains("FAIL", ignoreCase = true)
        return JudgeResult(if (passed) 1.0f else 0.5f, passed, result.take(300), emptyList())
    }

    override suspend fun answer(question: String, context: String): Flow<ExplanationChunk> {
        val (endpoint, apiKey, model) = getConfig()
        val result = chat(endpoint, apiKey, model, "Context: $context\nQ: $question")
        return flowOf(ExplanationChunk(result, true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    private suspend fun chat(endpoint: String, apiKey: String, model: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val url = endpoint.trimEnd('/') + "/chat/completions"
            val body = JSONObject()
            body.put("model", model)
            val msg = JSONObject()
            msg.put("role", "user")
            msg.put("content", prompt)
            val arr = JSONArray()
            arr.put(msg)
            body.put("messages", arr)
            body.put("temperature", 0.3)
            body.put("max_tokens", 2048)

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody(JSON))
                .build()

            val response: Response = client.newCall(request).execute()
            val bodyStr: String = response.body?.string() ?: throw IOException("Empty response")
            if (!response.isSuccessful) throw IOException("API ${response.code}: $bodyStr")

            val json = JSONObject(bodyStr)
            val choices: JSONArray = json.getJSONArray("choices")
            val first: JSONObject = choices.getJSONObject(0)
            val message: JSONObject = first.getJSONObject("message")
            message.getString("content")
        }
}
