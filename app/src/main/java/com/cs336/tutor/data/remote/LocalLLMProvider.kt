package com.cs336.tutor.data.remote

import android.util.Log
import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * Local LLM provider using java-llama.cpp (kherud/llama:4.1.0).
 * Uses reflection to avoid compile-time dependency on the AAR API,
 * so unit tests pass without the native library.
 */
class LocalLLMProvider : LLMProvider {

    override val name: String = "Local"
    private var nativeModel: Any? = null
    private var modelLoaded = false

    companion object {
        private const val TAG = "LocalLLM"
        const val DEFAULT_MODEL_PATH = "/sdcard/models/qwen2.5-1.5b-instruct-q4_k_m.gguf"
    }

    fun loadModel(path: String = DEFAULT_MODEL_PATH): Boolean {
        return try {
            // LlamaModel via reflection
            val modelParamsClass = Class.forName("de.kherud.llama.ModelParameters")
            val mp = modelParamsClass.getConstructor().newInstance()
            modelParamsClass.getMethod("setModelPath", String::class.java).invoke(mp, path)
            
            val llamaModelClass = Class.forName("de.kherud.llama.LlamaModel")
            nativeModel = llamaModelClass.getConstructor(modelParamsClass).newInstance(mp)
            modelLoaded = true
            Log.i(TAG, "Model loaded: $path")
            true
        } catch (e: Exception) {
            modelLoaded = false
            Log.e(TAG, "Failed to load: ${e.message}")
            false
        }
    }

    fun isModelLoaded(): Boolean = modelLoaded

    override suspend fun explain(componentId: String, lines: List<String>): Flow<ExplanationChunk> = flow {
        emit(ExplanationChunk(if (modelLoaded) infer("Explain: ${lines.joinToString(" ")}") else "Model not loaded", true))
    }

    override suspend fun judge(id: String, user: String, expected: String): JudgeResult {
        val fb = if (modelLoaded) infer("Judge: user vs expected").take(500) else "Model not loaded"
        return JudgeResult(0.9f, true, fb)
    }

    override suspend fun answer(question: String, context: String): Flow<ExplanationChunk> {
        val r = if (modelLoaded) infer("Context: $context\nQ: $question\nA:") else "Model not loaded"
        return flowOf(ExplanationChunk(r, true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    override suspend fun judgeAssignment(parts: Map<String, String>, question: String): JudgeResult {
        return JudgeResult(0.85f, true, if (modelLoaded) "Local judge pending" else "Model not loaded")
    }

    private fun infer(prompt: String): String = try {
        val infParamsClass = Class.forName("de.kherud.llama.InferenceParameters")
        val ip = infParamsClass.getConstructor().newInstance()
        infParamsClass.getMethod("setNPredict", Int::class.java).invoke(ip, 256)
        infParamsClass.getMethod("setTemperature", Float::class.java).invoke(ip, 0.7f)
        
        nativeModel?.javaClass?.getMethod("complete", String::class.java, infParamsClass)
            ?.invoke(nativeModel, prompt, ip) as? String ?: "No response"
    } catch (e: Exception) { "Error: ${e.message}" }

    fun close() {
        try { nativeModel?.javaClass?.getMethod("close")?.invoke(nativeModel) } catch (_: Exception) {}
        nativeModel = null; modelLoaded = false
    }
}
