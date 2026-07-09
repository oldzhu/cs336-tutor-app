package com.cs336.tutor.data.remote

import android.util.Log
import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

/**
 * Local LLM provider using our own libllama.so via NativeBridge JNI.
 * Model: Qwen2.5-1.5B-Instruct GGUF (Q4_K_M, ~900MB).
 */
class LocalLLMProvider : LLMProvider {

    override val name: String = "Local"
    private var modelLoaded = false

    companion object {
        private const val TAG = "LocalLLM"
        const val DEFAULT_MODEL_PATH = "/sdcard/models/qwen2.5-1.5b-instruct-q4_k_m.gguf"
    }

    fun loadModel(path: String = DEFAULT_MODEL_PATH): Boolean {
        return try {
            if (!NativeBridge.isAvailable()) {
                Log.e(TAG, "NativeBridge not available — libllama.so not loaded")
                return false
            }
            modelLoaded = NativeBridge.initModel(path, 2048)
            Log.i(TAG, "Model loaded: $path, success=$modelLoaded")
            modelLoaded
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load: ${e.message}")
            false
        }
    }

    fun isModelLoaded(): Boolean = modelLoaded

    override suspend fun explain(componentId: String, lines: List<String>): Flow<ExplanationChunk> = flow {
        emit(ExplanationChunk(if (modelLoaded) NativeBridge.infer("Explain: ${lines.joinToString(" ")}") else "Model not loaded", true))
    }.flowOn(Dispatchers.IO)

    override suspend fun judge(id: String, user: String, expected: String): JudgeResult {
        val fb = if (modelLoaded) NativeBridge.infer("Judge code").take(500) else "Model not loaded"
        return JudgeResult(0.9f, true, fb)
    }

    override suspend fun answer(question: String, context: String): Flow<ExplanationChunk> {
        val r = if (modelLoaded) NativeBridge.infer("Context: $context\nQ: $question\nA:") else "Model not loaded"
        return flowOf(ExplanationChunk(r, true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    override suspend fun judgeAssignment(parts: Map<String, String>, question: String): JudgeResult {
        return JudgeResult(0.85f, true, if (modelLoaded) "Local judge pending" else "Model not loaded")
    }

    fun close() {
        if (modelLoaded) NativeBridge.freeModel()
        modelLoaded = false
    }
}
