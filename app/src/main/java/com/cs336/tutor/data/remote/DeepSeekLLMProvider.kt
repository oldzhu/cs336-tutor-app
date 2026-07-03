package com.cs336.tutor.data.remote

import android.content.Context
import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
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
    private val gson = Gson()

    override val name: String = "DeepSeek"

    private fun getConfig(): Triple<String, String, String> {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val ep = prefs.getString("api_endpoint", "https://api.deepseek.com/v1") ?: "https://api.deepseek.com/v1"
        val key = prefs.getString("api_key", "") ?: ""
        val model = prefs.getString("model", "deepseek-v4-flash") ?: "deepseek-v4-flash"
        return Triple(ep, key, model)
    }

    private fun hasKey(): Boolean {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return (prefs.getString("api_key", "") ?: "").isNotEmpty()
    }

    override suspend fun explain(componentId: String, codeLines: List<String>): Flow<ExplanationChunk> = flow {
        val (ep, key, model) = getConfig()
        val prompt = "Explain each line of $componentId:\n" + codeLines.joinToString("\n")
        emit(ExplanationChunk(chat(ep, key, model, prompt), true))
    }

    override suspend fun judge(componentId: String, userCode: String, expectedCode: String): JudgeResult {
        if (!hasKey()) return JudgeResult(1.0f, true, "Configure API key in Settings for real evaluation.")
        val (ep, key, model) = getConfig()
        val prompt = "Compare code for $componentId.\nExpected:\n$expectedCode\n\nStudent:\n$userCode\n\nReply PASS or FAIL with short feedback."
        val result: String = chat(ep, key, model, prompt)
        val passed = result.contains("PASS", ignoreCase = true) && !result.contains("FAIL", ignoreCase = true)
        return JudgeResult(score = if (passed) 1.0f else 0.5f, passed = passed, feedback = result.take(300))
    }

    override suspend fun answer(question: String, ctx: String): Flow<ExplanationChunk> {
        if (!hasKey()) return flowOf(ExplanationChunk("Configure API key in Settings to enable Q&A."))
        val (ep, key, model) = getConfig()
        val result = chat(ep, key, model, "Context: $ctx\nQ: $question")
        return flowOf(ExplanationChunk(result, true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    private suspend fun chat(endpoint: String, apiKey: String, model: String, prompt: String): String {
        return withContext(Dispatchers.IO) {
            val url = endpoint.trimEnd('/') + "/chat/completions"
            val body = mapOf<Any, Any>(
                "model" to model,
                "messages" to listOf<Any>(mapOf<Any, Any>("role" to "user", "content" to prompt)),
                "temperature" to 0.3,
                "max_tokens" to 2048
            )
            val jsonBody: String = gson.toJson(body)

            val request: Request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody(JSON))
                .build()

            val response: Response = client.newCall(request).execute()
            val bodyStr: String = response.body?.string() ?: throw IOException("Empty response")
            if (!response.isSuccessful) throw IOException("API " + response.code + ": " + bodyStr)

            val root = JsonParser.parseString(bodyStr).asJsonObject
            val choices = root.getAsJsonArray("choices")
            val first = choices.get(0).asJsonObject
            val message = first.getAsJsonObject("message")
            val content: String = message.get("content").asString
            content
        }
    }
}
