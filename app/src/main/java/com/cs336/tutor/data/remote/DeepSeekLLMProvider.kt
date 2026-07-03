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
        val (ep, key, model) = getConfig()
        val p = "Explain each line of $componentId:\n" + codeLines.joinToString("\n")
        val r: String = call(ep, key, model, p)
        emit(ExplanationChunk(r, true))
    }

    override suspend fun judge(componentId: String, userCode: String, expectedCode: String): JudgeResult {
        val (ep, key, model) = getConfig()
        val p = "Compare code for $componentId.\nExpected:\n$expectedCode\n\nStudent:\n$userCode\n\nReply PASS or FAIL with feedback."
        val r: String = call(ep, key, model, p)
        val passed = r.contains("PASS", ignoreCase = true) && !r.contains("FAIL", ignoreCase = true)
        val score: Float = if (passed) 1.0f else 0.5f
        val fb: String = r.take(300)
        val empty: List<String> = emptyList()
        return JudgeResult(score, passed, fb, empty)
    }

    override suspend fun answer(question: String, ctx: String): Flow<ExplanationChunk> {
        val (ep, key, model) = getConfig()
        val r: String = call(ep, key, model, "Context: $ctx\nQ: $question")
        return flowOf(ExplanationChunk(r, true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    private suspend fun call(endpoint: String, apiKey: String, model: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val url = endpoint.trimEnd('/') + "/chat/completions"
            val root = JSONObject()
            root.put("model", model as Any)
            val msg = JSONObject()
            msg.put("role", "user" as Any)
            msg.put("content", prompt as Any)
            val arr = org.json.JSONArray()
            arr.put(msg as Any)
            root.put("messages", arr as Any)
            root.put("temperature", 0.3 as Any)
            root.put("max_tokens", 2048 as Any)

            val req = Request.Builder().url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(root.toString().toRequestBody(JSON)).build()
            val resp: Response = client.newCall(req).execute()
            val bodyStr: String = resp.body?.string() ?: throw IOException("Empty")
            if (!resp.isSuccessful) throw IOException("API error " + resp.code)
            val rj = JSONObject(bodyStr)
            val ch = rj.getJSONArray("choices")
            val fst = ch.getJSONObject(0)
            val m = fst.getJSONObject("message")
            m.getString("content")
        }
}
