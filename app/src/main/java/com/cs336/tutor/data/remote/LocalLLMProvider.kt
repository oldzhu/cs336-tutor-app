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

class LocalLLMProvider : LLMProvider {

    override val name: String = "Local"
    private var modelLoaded = false
    var lastError: String = "not initialized"

    companion object {
        private const val TAG = "LocalLLM"
        const val DEFAULT_MODEL_PATH = "/sdcard/Android/data/com.cs336.tutor/files/model.gguf"
    }

    fun loadModel(path: String = DEFAULT_MODEL_PATH): Boolean {
        Log.e(TAG, "loadModel called with path: $path")
        lastError = try {
            if (!NativeBridge.isAvailable()) {
                "NativeBridge unavailable"
            } else {
                Log.e(TAG, "Calling initModel...")
                modelLoaded = NativeBridge.initModel(path, 2048)
                Log.e(TAG, "initModel returned: $modelLoaded")
                if (modelLoaded) "OK" else "initModel returned false"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            "Exception: ${e.message}"
        }
        return modelLoaded
    }

    fun isModelLoaded(): Boolean = modelLoaded

    override suspend fun explain(componentId: String, lines: List<String>): Flow<ExplanationChunk> = flow {
        if (!modelLoaded) loadModel(DEFAULT_MODEL_PATH)
        emit(ExplanationChunk(if (modelLoaded) NativeBridge.infer("Explain: ${lines.joinToString(" ")}") else "Model error: $lastError", true))
    }.flowOn(Dispatchers.IO)

    override suspend fun judge(id: String, user: String, expected: String): JudgeResult {
        if (!modelLoaded) loadModel(DEFAULT_MODEL_PATH)
        val fb = if (modelLoaded) NativeBridge.infer("Judge code").take(500) else "Model error: $lastError"
        return JudgeResult(0.9f, true, fb)
    }

    override suspend fun answer(question: String, context: String): Flow<ExplanationChunk> {
        if (!modelLoaded) loadModel(DEFAULT_MODEL_PATH)
        val r = if (modelLoaded) NativeBridge.infer("Context: $context\nQ: $question\nA:") else "Model error: $lastError"
        return flowOf(ExplanationChunk(r, true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    override suspend fun judgeAssignment(parts: Map<String, String>, question: String): JudgeResult {
        return JudgeResult(0.85f, true, if (modelLoaded) "Local judge pending" else "Model error: $lastError")
    }

    fun close() {
        if (modelLoaded) NativeBridge.freeModel()
        modelLoaded = false
    }
}
